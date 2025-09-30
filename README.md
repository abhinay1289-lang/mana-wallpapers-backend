# Mana Wallpapers Backend

Spring Boot backend for the Mana Wallpapers marketplace.

## Features

- JWT Authentication & Authorization
- Stripe Payment Integration  
- AWS S3 File Storage
- Email Notifications
- RESTful API with OpenAPI Documentation
- PostgreSQL Database with Flyway Migrations
- Docker Support

## Quick Start

1. Install Prerequisites:
   - Java 17+
   - PostgreSQL 15+
   - Maven 3.6+

2. Setup Database:
   ```bash
   createdb manawallpapers
   ```

3. Configure Environment:
   - Copy application.properties and set your values
   - Set JWT_SECRET, STRIPE keys, AWS credentials

4. Run Application:
   ```bash
   ./mvnw spring-boot:run
   ```

5. API Documentation: http://localhost:8080/swagger-ui.html

## Environment Variables

Required for production:
- DATABASE_URL
- JWT_SECRET  
- STRIPE_SECRET_KEY
- STRIPE_WEBHOOK_SECRET
- AWS_ACCESS_KEY
- AWS_SECRET_KEY
- S3_BUCKET_NAME

## Deployment

### Docker
```bash
docker build -t mana-backend .
docker run -p 8080:8080 mana-backend
```

### Render
Use render.yaml configuration for one-click deployment.

## API Endpoints

- POST /auth/login - User login
- POST /auth/register - User registration
- GET /wallpapers - List wallpapers
- POST /wallpapers - Create wallpaper (Admin)
- POST /payment/checkout - Create payment session
- GET /download/{token} - Download wallpaper

## Testing

```bash
./mvnw test
```

## License

MIT License