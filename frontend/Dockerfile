FROM node:18-alpine as build
WORKDIR /app
# copy package files first for caching
COPY package.json package-lock.json* ./
# use npm install with safe flags inside the container
RUN npm install --legacy-peer-deps --silent
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
