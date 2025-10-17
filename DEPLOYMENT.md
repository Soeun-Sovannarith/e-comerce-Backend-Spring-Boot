# Production Deployment Guide

## 🚀 Production Deployment Checklist

### Prerequisites
- [ ] Docker and Docker Compose installed
- [ ] Production PostgreSQL database (or use included Docker setup)
- [ ] Domain name and SSL certificate (for HTTPS)
- [ ] Server with at least 2GB RAM

### Quick Start

1. **Clone your repository to production server**
   ```bash
   git clone <your-repo-url>
   cd backend
   ```

2. **Create production environment file**
   ```bash
   cp .env.prod.template .env.prod
   # Edit .env.prod with your production values
   ```

3. **Deploy with one command**
   ```bash
   ./deploy.sh
   ```

### Manual Deployment Steps

#### 1. Environment Configuration
Create `.env.prod` with your production values:
```env
DB_URL=jdbc:postgresql://your-db-host:5432/ecommerce_db
DB_USERNAME=your_db_user
DB_PASSWORD=your_secure_password
JWT_SECRET=your_very_long_random_jwt_secret
```

#### 2. Database Setup
- **Option A:** Use included Docker PostgreSQL
- **Option B:** Use external PostgreSQL database

For external database, run these SQL commands:
```sql
CREATE DATABASE ecommerce_db;
CREATE USER ecommerce_user WITH PASSWORD 'secure_password';
GRANT ALL PRIVILEGES ON DATABASE ecommerce_db TO ecommerce_user;
```

#### 3. Application Deployment

**Using Docker Compose (Recommended):**
```bash
# Build and start
docker-compose --env-file .env.prod up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f backend
```

**Using JAR file directly:**
```bash
# Build
./mvnw clean package -DskipTests

# Run with production profile
SPRING_PROFILES_ACTIVE=prod java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### Security Considerations

#### ✅ What's Already Configured
- [x] BCrypt password hashing
- [x] JWT authentication
- [x] SQL injection prevention (JPA)
- [x] Environment variables for secrets
- [x] Production logging configuration
- [x] Error message hiding in production

#### ⚠️ Additional Security Steps
1. **Use HTTPS** - Configure SSL/TLS certificate
2. **Firewall** - Only expose necessary ports (80, 443, 22)
3. **Database Security** - Use strong passwords, limit connections
4. **Regular Updates** - Keep OS and dependencies updated
5. **Monitoring** - Set up logging and monitoring

### Performance Optimization

#### JVM Settings
```bash
# For production, add these JVM options:
export JAVA_OPTS="-Xmx1g -Xms512m -XX:+UseG1GC"
```

#### Database Connection Pooling
The application uses HikariCP by default. For high traffic, tune these settings in `application-prod.properties`:
```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
```

### Monitoring and Health Checks

#### Health Check Endpoint
```bash
curl http://your-domain:8080/actuator/health
```

#### Application Logs
```bash
# Docker logs
docker-compose logs -f backend

# If running JAR directly
tail -f logs/application.log
```

### Backup Strategy

#### Database Backup
```bash
# Create backup
docker exec ecommerce-postgres pg_dump -U postgres ecommerce_db > backup_$(date +%Y%m%d).sql

# Restore backup
docker exec -i ecommerce-postgres psql -U postgres ecommerce_db < backup_20241017.sql
```

### Troubleshooting

#### Common Issues
1. **Port 8080 already in use**
   ```bash
   # Check what's using the port
   lsof -i :8080
   # Change port in .env.prod: PORT=8081
   ```

2. **Database connection failed**
   - Check database is running: `docker-compose ps`
   - Verify credentials in `.env.prod`
   - Check network connectivity

3. **JWT token issues**
   - Ensure JWT_SECRET is at least 32 characters
   - Check token expiration settings

#### Logs Location
- Docker: `docker-compose logs backend`
- JAR: Check console output or configure logging file

### SSL/HTTPS Setup (Recommended)

#### Using Nginx Reverse Proxy
```nginx
server {
    listen 80;
    server_name your-domain.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl;
    server_name your-domain.com;

    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### Production Environment Variables

Required variables for `.env.prod`:
```env
# Database
DB_URL=jdbc:postgresql://host:5432/dbname
DB_USERNAME=username
DB_PASSWORD=secure_password

# JWT
JWT_SECRET=very-long-random-string-at-least-32-chars
JWT_EXPIRATION=86400000

# Server
PORT=8080
```

### Deployment Platforms

#### AWS EC2
1. Launch EC2 instance (t3.medium recommended)
2. Install Docker and Docker Compose
3. Clone repository and follow deployment steps
4. Configure security groups (ports 80, 443, 22)

#### DigitalOcean Droplet
1. Create droplet with Docker pre-installed
2. Follow deployment steps
3. Configure firewall

#### VPS (General)
1. Ensure server has Docker installed
2. Open necessary ports in firewall
3. Follow deployment steps

### Contact and Support
- Application logs: Check Docker logs or console output
- Database issues: Verify connection strings and credentials
- Performance: Monitor with `docker stats` or application metrics
