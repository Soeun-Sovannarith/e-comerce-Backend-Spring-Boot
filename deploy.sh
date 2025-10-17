#!/bin/bash

# Production Deployment Script
# Run this script to deploy your application to production

echo "🚀 Starting production deployment..."

# Check if .env.prod exists
if [ ! -f ".env.prod" ]; then
    echo "❌ Error: .env.prod file not found!"
    echo "📝 Please copy .env.prod.template to .env.prod and fill in your production values"
    exit 1
fi

# Load production environment variables
export $(cat .env.prod | xargs)

# Validate required environment variables
if [ -z "$DB_PASSWORD" ] || [ -z "$JWT_SECRET" ]; then
    echo "❌ Error: Required environment variables not set in .env.prod"
    echo "📝 Please ensure DB_PASSWORD and JWT_SECRET are configured"
    exit 1
fi

# Build and start services
echo "🏗️  Building application..."
docker-compose down
docker-compose build --no-cache

echo "🐳 Starting production services..."
docker-compose --env-file .env.prod up -d

# Wait for services to be ready
echo "⏳ Waiting for services to start..."
sleep 30

# Check if services are running
if docker-compose ps | grep -q "Up"; then
    echo "✅ Deployment successful!"
    echo "🌐 Application is running at: http://localhost:8080"
    echo "📊 Health check: curl http://localhost:8080/actuator/health"
else
    echo "❌ Deployment failed. Check logs with: docker-compose logs"
    exit 1
fi
