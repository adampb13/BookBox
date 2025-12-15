# 1. Build frontu
FROM node:20-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

# 2. Serwowanie statyczne przez nginx
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html

# prosty config: serwujemy statyczny frontend na porcie 80
EXPOSE 80
