# Campus Equipment Booking Service

A Java 17 / Spring Boot REST API developed as part of a DevOps CI/CD lab assessment. The project demonstrates REST API development, input validation, unit testing, Git-based version control, automated builds with Jenkins, Docker containerization, and Kubernetes deployment using Minikube.

## Technology Stack

- **Language:** Java 17
- **Framework:** Spring Boot
- **Build Tool:** Maven
- **Testing:** JUnit 5
- **Version Control:** Git and GitHub
- **CI/CD:** Jenkins Declarative Pipeline
- **Containerization:** Docker
- **Orchestration:** Kubernetes
- **Local Kubernetes Environment:** Minikube
- **Container Registry:** Local Docker image / Minikube image cache

## Project Features

- Retrieve available equipment and equipment details.
- Create equipment bookings.
- Retrieve booking details.
- Cancel existing bookings.
- Validate borrower identifiers.
- Validate booking quantities and inventory availability.
- Restore equipment inventory when a booking is cancelled.
- Monitor application health through Spring Boot Actuator.

## REST API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/equipment` | List available equipment |
| GET | `/api/equipment/{id}` | Retrieve equipment details |
| POST | `/api/bookings` | Create a booking |
| GET | `/api/bookings/{id}` | Retrieve booking details |
| POST | `/api/bookings/{id}/cancel` | Cancel a booking |
| GET | `/actuator/health` | Check application health |

## Validation and Inventory Management

The application implements the following validation rules:

- Reject booking quantities that are zero or negative.
- Reject booking requests exceeding available inventory.
- Allow bookings for the exact quantity available.
- Reject missing or blank borrower identifiers.
- Validate booking requests before creating bookings or reducing inventory.
- Restore inventory when a booking is cancelled.

These checks help maintain inventory consistency and prevent invalid booking requests.

## Project Structure

```text
Campus-Equipment-Booking-Student-Starter/
├── src/
│   ├── main/
│   │   └── java/
│   └── test/
├── k8s/
│   ├── deployment.yaml
│   └── service.yaml
├── Dockerfile
├── Jenkinsfile
├── pom.xml
└── README.md
```

## Prerequisites

Install the following tools:

- Java 17 or a compatible configured JDK
- Apache Maven
- Git
- Docker Desktop
- Minikube
- kubectl
- Jenkins

The Jenkins installation must have a configured JDK and Maven installation. The current Jenkins pipeline uses the tool names `JDK21` and `Maven3`.

## 1. Clone the Repository

Clone the GitHub repository:

```bash
git clone https://github.com/Haricodex/Campus-Equipment-Booking.git
cd Campus-Equipment-Booking
```

Check the available branches:

```bash
git branch -a
```

The development branch used during this lab is `feature/borrower-validation`.

## 2. Build and Test with Maven

Run the unit tests:

```bash
mvn clean test
```

Package the application:

```bash
mvn clean package
```

The executable JAR is generated at:

```text
target/campus-equipment-booking-1.0.0.jar
```

## 3. Run Locally

Start the application using Maven:

```bash
mvn spring-boot:run
```

The application runs on port `8080`.

Check the health endpoint:

```text
http://localhost:8080/actuator/health
```

The expected health response indicates that the application is `UP`.

## 4. Version Control with Git

The project uses Git for version control and GitHub for remote source-code hosting.

Check the current working tree:

```bash
git status
```

Create and switch to the development branch:

```bash
git switch -c feature/borrower-validation
```

Stage and commit changes:

```bash
git add .
git commit -m "Describe your changes"
```

Push changes to GitHub:

```bash
git push
```

For the first push of a new branch, use:

```bash
git push -u origin feature/borrower-validation
```

## 5. Continuous Integration with Jenkins

The project includes a `Jenkinsfile` that defines a Jenkins Declarative Pipeline.

### Pipeline Stages

**Checkout Project**
- Jenkins retrieves the project from the configured GitHub repository through Pipeline script from SCM.

**Build and Test**
- Executes `mvn clean test`.
- Runs the project tests and reports build failures.

**Package**
- Executes `mvn package`.
- Generates the executable application JAR.

### Jenkins Configuration

The Jenkins job is named:

```text
Campus-Equipment-Booking-Pipeline
```

The job is configured with:

- Pipeline definition: Pipeline script from SCM
- Source control: Git
- Repository: `https://github.com/Haricodex/Campus-Equipment-Booking.git`
- Branch: `feature/borrower-validation`
- Script path: `Jenkinsfile`
- JDK installation: `JDK21`
- Maven installation: `Maven3`

The pipeline reports success when the build, tests, and packaging complete successfully. If a stage fails, the Jenkins build reports failure and the Console Output provides diagnostic information.

### GitHub Webhook Integration

A GitHub webhook has been configured to send push-event notifications to Jenkins through an ngrok HTTPS tunnel.

The Jenkins job has **GitHub hook trigger for GITScm polling** enabled.

When the webhook endpoint is reachable and the integration is running correctly, pushing a new commit to the configured branch can trigger a Jenkins build automatically.

**Note:** The ngrok tunnel must remain active for GitHub to reach a locally hosted Jenkins instance. A successful webhook delivery confirms that GitHub delivered the event; verify the Jenkins build history to confirm that the event triggered a build.

## 6. Docker Containerization

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

View running containers:

```bash
docker ps
```

Stop the container when finished:

```bash
docker stop campus-booking-container
```

Remove the stopped container if required:

```bash
docker rm campus-booking-container
```

The Dockerfile uses a Java 17 runtime image, copies the packaged JAR into the image, exposes port `8080`, and starts the application.

## 7. Kubernetes Deployment with Minikube

Start Minikube using Docker as the driver:

```bash
minikube start --driver=docker
```

Build the Docker image before loading it into Minikube:

```bash
docker build -t campus-equipment-booking:set-d .
```

Load the image into Minikube:

```bash
minikube image load campus-equipment-booking:set-d
```

Apply the Kubernetes Deployment and Service configurations:

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

Verify rollout status and replica readiness:

```bash
kubectl rollout status deployment/campus-equipment-booking
kubectl get deployments
kubectl get pods
```

The Deployment was verified with two running application replicas.

### Access the Application

Forward a local port to the Kubernetes Service:

```bash
kubectl port-forward service/campus-equipment-booking-service 8082:8080
```

Keep this command running in the terminal. Open a second terminal and check application health:

```bash
curl http://localhost:8082/actuator/health
```

The expected health status is `UP`.

To stop port forwarding, press `Ctrl+C` in the terminal running the command.

### Kubernetes Configuration

- `k8s/deployment.yaml` defines the application Deployment.
- `k8s/service.yaml` exposes the application through a Kubernetes Service.
- Docker image: `campus-equipment-booking:set-d`
- Container port: `8080`
- Replicas: `2` after scaling.

## 8. Current CI/CD Workflow

The project demonstrates the following workflow:

1. Develop and update the Spring Boot application.
2. Commit source-code changes using Git.
3. Push commits to GitHub.
4. Trigger Jenkins manually or through the configured GitHub webhook when the integration is active.
5. Fetch the source code from GitHub.
6. Run Maven tests.
7. Package the application into an executable JAR.
8. Build and run the Docker image.
9. Deploy the application to Kubernetes using Minikube.
10. Verify application health and deployment status.

**Scope note:** The current Jenkins pipeline automates source checkout, testing, and packaging. Docker image creation and Kubernetes deployment are performed separately using the documented commands; they are not yet automated stages in the Jenkins pipeline.

## Notes and Limitations

- Equipment inventory and booking information are stored in memory and reset when the application restarts.
- No database is required for this lab implementation.
- Minikube provides a local Kubernetes environment for development and testing.
- The GitHub webhook depends on the availability of the ngrok tunnel while Jenkins is running locally.
- For secure or long-term use, Jenkins should require authentication and the public tunnel should be protected appropriately.
