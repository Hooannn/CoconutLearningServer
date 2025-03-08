![enter image description here](https://img.freepik.com/free-vector/happy-freelancer-with-computer-home-young-man-sitting-armchair-using-laptop-chatting-online-smiling-vector-illustration-distance-work-online-learning-freelance_74855-8401.jpg)

## ELearning Platform Document

#### Overview:

I've developed a full-stack web application using Spring Boot for the backend and React for the frontend. The
application is deployed using Compute Engine on Google Cloud Platform (GCP). Below is an overview of the architecture,
technologies used, CI/CD setup, and deployment process.

#### Architecture:

- **Backend**: The backend of the application is built with Spring Boot, a popular Java-based framework for building
  robust and scalable web applications. It handles data processing, business logic, and interacts with the database,  using cronjob for email reminder

- **Frontend**: The frontend of the application is developed using React, a JavaScript library for building user
  interfaces. It provides a responsive and interactive user experience.

- **Database**: The application uses Postgres for storing and managing data. Redis for storing some metadata (refresh
  token, reset password secret,...)

- **Search engine**: Simple integration with Elasticsearch for users lookup.

- **File server**: Using open-source self-hosted file server [seaweedfs](https://github.com/seaweedfs/seaweedfs)

- **Notification**:
    + For mail notifications: using [sendgrid](https://www.sendgrid.com).
    + For realtime notifications: using [socket.io](https://socket.io/)
    + For push notifications: using [Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging)

- **Jitsi as a Service**: Integrate with [JaaS](https://jaas.8x8.vc/#/) for meeting feature. Embed Jitsi meeting to
  React client, manage meeting tokens in Spring boot server.

- **Portainer**: Using [portainer](https://github.com/portainer/portainer) for Docker management

- **Nginx**: In the hosted VPS, using nginx to forward all requests to suitable domains
    + https://socket.eclass.ink: For socket.io server
    + https://classroom.eclass.ink: For React app
    + https://api.eclass.ink: For Spring boot API
    + https://file.eclass.ink: For seaweedfs file server
    + https://portainer.eclass.ink: for Docker management

#### CI/CD Setup:

##### Deployment: [Deployment](https://github.com/Hooannn/ELearningDeployment)

##### Backend: [Server](https://github.com/Hooannn/ELearningServer)

##### Frontend: [Client](https://github.com/Hooannn/ELearningClient)

- **GitHub Repository**: The source code for the application is hosted on GitHub in separate repositories for the
  backend and frontend and deployment.

- **Continuous Integration (CI)**: GitHub Actions is configured to automatically build, test, and package the
  application whenever changes are pushed to the respective repositories.

- **Continuous Deployment (CD)**: Docker Hub is used for storing Docker images. GitHub Actions builds Docker images for
  both the backend and frontend, and then pushes them to Docker Hub. After that call the trigger action on the
  deployment repository. The deployment repository pulls the latest Docker images from Docker Hub and uses Docker
  Compose to orchestrate the deployment of the application on the VPS hosted on GCP.

### Features of the Application:

This ELearning Platform is a powerful tool designed for educators to manage classes, assignments, and communication with
students. Here are some of its key features:

1. **Course Management**: Teachers can create and organize classes, add students, and manage multiple courses from a
   single dashboard.
2. **Assignment Distribution**: Teachers can create and distribute assignments, including instructions, due dates, and
   attachments such as documents, links, or videos.
3. **Submission and Grading**: Students can submit assignments electronically, and teachers can review and grade them
   within this ELearning Platform. Teachers can provide feedback and comments directly on assignments.
4. **Classroom Insights**: Teachers have access to analytics and insights, including assignment completion rates, and
   grading summaries, to track student progress and identify areas for improvement.
5. **Announcements and Communication**: Teachers can schedule meetings, including start time, end time for all members
   in classroom to participate in.
6. **Realtime notifications**: Users can receive realtime updates and notifications, also mail notifications and push
   notifications.

### API Design: [Swagger UI](https://api.eclass.ink/swagger-ui/index.html)

### Database Design:

![enter image description here](https://i.ibb.co/rm0DrhT/default-public.png)
