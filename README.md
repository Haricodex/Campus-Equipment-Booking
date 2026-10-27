# Campus Equipment Booking Service

Java 17 / Spring Boot REST API for a CI/CD lab assessment.

## Prerequisites

- Java 17
- Maven
- Docker Desktop
- Minikube
- kubectl

## Build and Test

Run the unit tests:

```bash
mvn clean test
```

Package the application and generate the executable JAR:

```bash
mvn clean package
```

The generated JAR is located at:

`target/campus-equipment-booking-1.0.0.jar`

## Run Locally

Start the application using Maven:

```bash
mvn spring-boot:run
```

The application runs on port 8080.

## REST API Endpoints

- `GET /api/equipment` — List available equipment.
- `GET /api/equipment/{id}` — Get equipment details.
- `POST /api/bookings` — Create a booking.
- `GET /api/bookings/{id}` — Get booking details.
- `POST /api/bookings/{id}/cancel` — Cancel a booking.
- `GET /actuator/health` — Check application health.

## Validation and Inventory Fixes

- Reject booking quantities that are zero or negative.
- Reject requests exceeding the available inventory.
- Allow booking the exact available quantity.
- Reject missing or blank borrower identifiers.
- Validate booking requests before reducing inventory or creating a booking.
- Restore inventory when a booking is cancelled.

## Docker

Build the Docker image from the project root:

```bash
docker build -t campus-equipment-booking:set-d .
```

Run the container:

```bash
docker run -d --name campus-booking-container -p 8080:8080 campus-equipment-booking:set-d
```

Check application health:

```bash
curl http://localhost:8080/actuator/health
```

Stop the container when finished:

```bash
docker stop campus-booking-container
```

## Kubernetes with Minikube

Start Minikube:

```bash
minikube start --driver=docker
```

Load the Docker image into Minikube:

```bash
minikube image load campus-equipment-booking:set-d
```

Apply the Deployment and Service configurations:

```bash
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

Check the Deployment, Pods, and Service:

```bash
kubectl get deployments
kubectl get pods
kubectl get services
```

Scale the application to two replicas:

```bash
kubectl scale deployment campus-equipment-booking --replicas=2
```

Verify that both replicas are ready:

```bash
kubectl rollout status deployment/campus-equipment-booking
kubectl get deployments
kubectl get pods
```

Access the application through the Kubernetes Service using port forwarding:

```bash
kubectl port-forward service/campus-equipment-booking-service 8081:8080
```

Keep port forwarding running and open another terminal to check the health endpoint:

```bash
curl http://localhost:8081/actuator/health
```

The expected health status is `UP`.

## Kubernetes Configuration

- `k8s/deployment.yaml` — Defines the application Deployment and container image.
- `k8s/service.yaml` — Exposes the application through a NodePort Service.
- Docker image: `campus-equipment-booking:set-d`
- Container port: `8080`
- Kubernetes replicas: 2 after scaling.

## Notes

Equipment inventory and bookings are stored in memory and reset when the application restarts. No database is required.