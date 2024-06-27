This project is a full-stack web application designed to provide users with a comprehensive movie browsing and purchasing experience. 

Users are able to log in using their credentials, browse movies by multiple categories, full-text searching with autocomplete and by specific fields, add movies to their shopping cart, and checking them out using payment credentials.



**Key Features:**

- Backend Development: Implemented a RESTful API backend using Java Servlets and JDBC to ensure smooth data management and interaction with the MySQL database. This backend handles user authentication, movie CRUD operations, and transaction processing.

- Frontend Interfaces: Created responsive HTML/CSS and JavaScript frontend interfaces that interact with the backend API via AJAX calls. This ensures dynamic content updates and a seamless user experience on Apache Tomcat.

- Security Measures: Integrated ReCAPTCHA for improved user authentication and protection against automated abuse and spam. Utilized prepared statements in JDBC to prevent SQL injection attacks by separating SQL code from user input. Additionally, ensured secure storage of user passwords through encryption techniques, thereby safeguarding sensitive information stored in the database.

- Scalability and High Availability: Employed MySQL with master-slave replication and utilized AWS Elastic Load Balancing to ensure scalability and high availability of the application.

- XML Parsing Capability: Integrated XML parsing functionality to efficiently add tens of thousands of movies and stars into the database.

- Containerization and Deployment: Deployed the application as a containerized solution using Docker on an AWS Kubernetes (K8s) cluster. This setup achieved impressive performance metrics, handling up to 3,261 requests per minute during Apache JMeter stress tests.



**Technologies Used:**

- Backend: Java, Java Servlets, JDBC, MySQL

- Frontend: HTML, CSS, JavaScript, AJAX

- Deployment: Docker, AWS (Kubernetes, Elastic Load Balancing)


    
