# Introduction

Sample ecommerce app using spring boot (Security, Web, JPA).

# Pre-requisites

# Usage

admin user credentials: admin@shopamos.com / Password123!
sysadmin db credentials: root / password1!

# Future work
- Create new I/O objects for the service layer. Because it's not a good practice to return managed entities between service <-> controller. 
- status at the product level instead of order level for a more refined order tracking
- isolate admin role logic to dedicated paths */admin/*. Because distinguishing admins from customers within same handler method is not scalable and more error prone.
- concurrency control with optimistic locking as well as using @Transaction at the service layer.
- use logging aspects.
- strengthen unit tests by forcing coverage during build and integrate pit mutation testing.
- end-to-end tests using @SprintBootTest or cucumber.
- adding pagination functionality when retrieving products / orders / accounts.
- add audit columns to database tables (createdBy, updatedBy, createdDate, updatedDate).
- react client application.
