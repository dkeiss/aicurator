# frontend/Dockerfile
FROM node:18-alpine AS build
WORKDIR /app
COPY package.json yarn.lock ./
RUN yarn install
COPY . .
RUN yarn build

CMD ["yarn", "start"]