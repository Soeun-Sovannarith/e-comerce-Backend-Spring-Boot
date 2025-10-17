# DigitalOcean Deployment Guide for E-Commerce Backend

## 🚀 DigitalOcean Deployment Options

### Option 1: DigitalOcean App Platform (Recommended - PaaS)
- Easiest deployment with automatic scaling
- Built-in CI/CD from GitHub
- Managed database available

### Option 2: DigitalOcean Droplet (VPS)
- Full control over the server
- More cost-effective for consistent workloads
- Requires more setup

---

## 📋 Prerequisites

1. **DigitalOcean Account** - [Sign up here](https://digitalocean.com)
2. **GitHub Repository** - Push your code to GitHub
3. **Domain Name** (optional but recommended)

---

## 🎯 Option 1: App Platform Deployment (Recommended)

### Step 1: Prepare Your Repository

1. **Push your code to GitHub:**
   ```bash
   git add .
   git commit -m "Prepare for DigitalOcean deployment"
   git push origin main
   ```

2. **Ensure these files are in your repo:**
   - `Dockerfile` ✅
   - `docker-compose.yml` ✅
   - `.env.prod.template` ✅

### Step 2: Create App Platform Application

1. **Go to DigitalOcean Console**
   - Navigate to [DigitalOcean Apps](https://cloud.digitalocean.com/apps)
   - Click "Create App"

2. **Connect GitHub Repository**
   - Select "GitHub" as source
   - Authorize DigitalOcean to access your GitHub
   - Choose your repository
   - Select branch: `main`
   - Enable "Autodeploy" for automatic deployments

3. **Configure the Application**
   - App Name: `ecommerce-backend`
   - Region: Choose closest to your users
   - Plan: Start with Basic ($5/month)

### Step 3: Environment Variables

Add these environment variables in the App Platform:

```env
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:postgresql://your-db-host:5432/ecommerce_db
DB_USERNAME=your_db_user
DB_PASSWORD=your_secure_password
JWT_SECRET=your_jwt_secret_from_env_file
JWT_EXPIRATION=86400000
PORT=8080
```

### Step 4: Database Setup

**Option A: DigitalOcean Managed Database (Recommended)**
1. Go to Databases → Create Database
2. Choose PostgreSQL
3. Select same region as your app
4. Choose Basic plan ($15/month)
5. Copy connection details to your environment variables

**Option B: Database in same container (Not recommended for production)**
- Use the docker-compose.yml approach

---

## 🖥️ Option 2: Droplet Deployment (VPS)

### Step 1: Create Droplet

1. **Create Droplet:**
   - Go to DigitalOcean → Create → Droplet
   - **Image:** Ubuntu 22.04 LTS
   - **Size:** Basic, Regular, $12/month (2GB RAM, 1 vCPU)
   - **Region:** Choose closest to your users
   - **Authentication:** SSH Key (recommended) or Password
   - **Hostname:** `ecommerce-backend`

2. **Add Firewall Rules:**
   - SSH (22)
   - HTTP (80)
   - HTTPS (443)
   - Custom: 8080 (for direct access during setup)

### Step 2: Server Setup

**Connect to your droplet:**
```bash
ssh root@your_droplet_ip
```

**Install Docker and Docker Compose:**
```bash
# Update system
apt update && apt upgrade -y

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh

# Install Docker Compose
apt install docker-compose-plugin -y

# Start Docker
systemctl start docker
systemctl enable docker

# Install Git
apt install git -y

# Install Nginx (for reverse proxy)
apt install nginx -y
```

### Step 3: Deploy Application

```bash
# Clone your repository
git clone https://github.com/yourusername/your-repo-name.git
cd your-repo-name

# Create production environment file
cp .env.prod.template .env.prod

# Edit with your production values
nano .env.prod
```

**Add your production values to .env.prod:**
```env
DB_URL=jdbc:postgresql://localhost:5432/ecommerce_db
DB_USERNAME=postgres
DB_PASSWORD=your_secure_password
JWT_SECRET=your_jwt_secret_from_local_env
JWT_EXPIRATION=86400000
PORT=8080
```

**Deploy the application:**
```bash
# Make deploy script executable
chmod +x deploy.sh

# Run deployment
./deploy.sh
```

### Step 4: Configure Nginx Reverse Proxy

Create Nginx configuration:
```bash
nano /etc/nginx/sites-available/ecommerce-backend
```

Add this configuration:
```nginx
server {
    listen 80;
    server_name your-domain.com;  # Replace with your domain or droplet IP
    
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

**Enable the site:**
```bash
# Enable site
ln -s /etc/nginx/sites-available/ecommerce-backend /etc/nginx/sites-enabled/

# Test configuration
nginx -t

# Restart Nginx
systemctl restart nginx
```

---

## 🔒 SSL Certificate Setup (HTTPS)

### Using Certbot (Let's Encrypt)

```bash
# Install Certbot
apt install certbot python3-certbot-nginx -y

# Get SSL certificate
certbot --nginx -d your-domain.com

# Auto-renewal (already configured by certbot)
certbot renew --dry-run
```

---

## 📊 Monitoring and Maintenance

### Health Checks

```bash
# Check application health
curl http://your-domain.com/actuator/health

# Check Docker containers
docker-compose ps

# View logs
docker-compose logs -f backend
```

### Backup Database

```bash
# Create backup script
cat > /root/backup.sh << 'EOF'
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
docker exec ecommerce-postgres pg_dump -U postgres ecommerce_db > /root/backups/backup_$DATE.sql
# Keep only last 7 days
find /root/backups -name "backup_*.sql" -mtime +7 -delete
EOF

# Make executable
chmod +x /root/backup.sh

# Create backups directory
mkdir -p /root/backups

# Add to crontab (daily backup at 2 AM)
(crontab -l 2>/dev/null; echo "0 2 * * * /root/backup.sh") | crontab -
```

---

## 🚦 Deployment Checklist

### Pre-deployment:
- [ ] Code pushed to GitHub
- [ ] Environment variables configured
- [ ] Database credentials ready
- [ ] Domain name configured (if using custom domain)

### Post-deployment:
- [ ] Application accessible via URL
- [ ] Health check endpoint working: `/actuator/health`
- [ ] Database connection successful
- [ ] JWT authentication working
- [ ] SSL certificate installed (HTTPS)
- [ ] Monitoring and backups configured

---

## 💰 Cost Estimation

### App Platform:
- Basic App: $5/month
- Managed Database: $15/month
- **Total: ~$20/month**

### Droplet:
- Droplet (2GB): $12/month
- **Total: ~$12/month** (if you manage your own database)

---

## 🆘 Troubleshooting

### Common Issues:

1. **Port 8080 blocked:**
   ```bash
   ufw allow 8080
   ```

2. **Database connection failed:**
   ```bash
   # Check if PostgreSQL is running
   docker-compose ps
   
   # Check logs
   docker-compose logs postgres
   ```

3. **Application not starting:**
   ```bash
   # Check application logs
   docker-compose logs backend
   
   # Check environment variables
   docker-compose exec backend env | grep -E 'DB_|JWT_'
   ```

4. **Memory issues:**
   ```bash
   # Check memory usage
   free -h
   
   # Restart services
   docker-compose restart
   ```

---

## 📞 Next Steps

1. Choose your deployment method (App Platform or Droplet)
2. Follow the respective guide above
3. Test your deployment
4. Set up monitoring and backups
5. Configure your frontend to use the new backend URL

Would you like me to help you with any specific step or create additional configuration files?
