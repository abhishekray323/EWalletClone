# EWallet Clone  
A practice open-source project that simulates
the core functionalities of a digital wallet application. It demonstrates my skills
to implement scalable backend services, while I navigate this learning journey.


## Key Features
Here are some of the key features of EWallet Clone:

✅ User Registration & Login (Spring Security, OAuth2)

✅ JWT-based access tokens with **secure refresh tokens**  
- Refresh tokens are stored **hashed with SHA-256** (using `java.security.MessageDigest`)  
- A scheduled job runs daily to **delete refresh tokens 30 days after they are revoked or expired**

✅ KYC Verification Endpoint

✅ Wallet Creation & Balance Management

✅ Transfer Funds from one user to another (sufficient balance check, etc.)

✅ Transaction History API


## Design Doc
Here is the design doc of project:
https://docs.google.com/document/d/1ta7aKL0uuUX88ZTPJEOiNPtboIRDsWsUVeWgkaz2bTg/edit?usp=sharing

## Core of Project
The above features encompasses the current scope of the project, it will evolve over time.
The core idea of this project is to practice basics of building scalable backend services, and
hence the goals of project will evolve as I discover my next learning objectives. Below are 
my current practicing objectives:
1. Practicing to design and implement RESTful APIs using Spring Boot.
2. Security: Implement authentication and authorization using Spring Security and OAuth2.
3. MySQL database design and optimization.
4. Distributed caching strategies using Redis.
5. Learning Testing using JUnit and Mockito.
6. Microservices architecture and inter-service communication.

## AI alert:
In  playlist "VideoRecords" [playlist link](https://www.youtube.com/playlist?list=PLbG4_ah2VvJnjxUtDkQS8jn-ra9AUUUra), I have compiled videos while coding this project to demonstrate how I am learning
to ride the dragon of github-copilot ( you can't just accept whatever suggestions it gives
because then you won't learn new technologies, you won't be able to review the code it generates
, you will loose critical thinking power , but  you still have to make sure
that you harness the predictive power of AI, and let you deliver world-class software faster
. To me, it's like learning how to ride a dragon).


### Future perspectives:
1. Learn system design principles and practice it over the services implemented here.
2. Monitoring , Logging and Measuring performance are key to identifying bottlenecks and ensuring the reliability of services. It's a whole art in itself. I wish to practice this over here.
3. Will simulate traffic using tools like JMeter or Locust to see how the system performs under load. Then would identify bottlenecks and optimize performance iteratively.
