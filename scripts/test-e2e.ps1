# End-to-end Smoke Test Script for BookBox backend
# Usage: Open PowerShell in repo root and run: .\scripts\test-e2e.ps1
# This script:
# - Waits for backend to respond on http://localhost:8080/api/books
# - Registers a new test user
# - Fetches books and picks the first book
# - Creates a loan for that user and book
# - Verifies the loan via API and directly in MySQL container

param(
    [int]$WaitSeconds = 2,
    [int]$Retries = 30
)

function Wait-ForBackend {
    param($retries, $wait)
    $i = 0
    while ($i -lt $retries) {
        try {
            Invoke-RestMethod -Uri http://localhost:8080/api/books -Method Get -TimeoutSec 2 | Out-Null
            Write-Host "Backend is responding"
            return $true
        } catch {
            Start-Sleep -Seconds $wait
            $i++
        }
    }
    Write-Error "Backend did not start within expected time"
    return $false
}

# 1) Wait for backend
if (-not (Wait-ForBackend -retries $Retries -wait $WaitSeconds)) { exit 2 }

# 2) Register a user
$rand = [System.Guid]::NewGuid().ToString().Substring(0,8)
$email = "e2e-$rand@example.com"
$registerBody = @{ email = $email; password = 'Test123!'; name = 'E2E Test' } | ConvertTo-Json
try {
    $reg = Invoke-RestMethod -Uri http://localhost:8080/api/users/register -Method Post -ContentType 'application/json' -Body $registerBody -TimeoutSec 10
    Write-Host "Registered user id: $($reg.id)"
} catch {
    Write-Error "Failed to register user: $_"
    exit 3
}

# 3) Fetch books
try {
    $books = Invoke-RestMethod -Uri http://localhost:8080/api/books -Method Get -TimeoutSec 10
} catch {
    Write-Error "Failed to fetch books: $_"
    exit 4
}
if (-not $books -or $books.Count -eq 0) {
    Write-Error "No books available from /api/books"
    exit 5
}
$bookId = $books[0].id
Write-Host "Using book id: $bookId - $($books[0].title)"

# 4) Create a loan
$loanBody = @{ userId = $reg.id; bookId = $bookId } | ConvertTo-Json
try {
    $loan = Invoke-RestMethod -Uri http://localhost:8080/api/loans -Method Post -ContentType 'application/json' -Body $loanBody -TimeoutSec 10
    Write-Host "Created loan id: $($loan.id)"
} catch {
    Write-Error "Failed to create loan: $_"
    exit 6
}

# 5) Verify loan via API
try {
    $loans = Invoke-RestMethod -Uri "http://localhost:8080/api/loans/user/$($reg.id)" -Method Get -TimeoutSec 10
    if (-not ($loans | Where-Object { $_.id -eq $loan.id })) {
        Write-Error "Loan not found via API"
        exit 7
    }
    Write-Host "Loan verified via API"
} catch {
    Write-Error "Failed to verify loan via API: $_"
    exit 7
}

# 6) Verify loan directly in MySQL
try {
    $query = "USE bbox_library; SELECT id, user_id, book_id FROM loans WHERE id=$($loan.id);"
    $output = docker exec -i bbox_db mysql -uroot -prootpass -e $query 2>&1
    if ($LASTEXITCODE -ne 0) {
        Write-Error "MySQL query failed: $output"
        exit 8
    }
    Write-Host "MySQL row:
$output"
} catch {
    Write-Error "Failed to query MySQL: $_"
    exit 8
}

Write-Host "E2E script succeeded"
exit 0
