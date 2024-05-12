to perform the operations in Postman with the role-based authorization implemented.
1. Authentication

By default a admin entry is created with username : admin and password : password

Admin Authentication

Create a new request with method POST and URL http://localhost:8080/game/auth/admin.
Go to the Body tab, select x-www-form-urlencoded, and add the following key-value pairs:

username: Admin's username
password: Admin's password


Click Send. If the credentials are correct, you should receive a 200 OK response with the message "Admin authenticated".


Employee Authentication

Follow the same steps as above, but use the URL http://localhost:8080/game/auth/employee and provide an employee's username and password.



2. Admin Operations

Create Admin

Create a new request with method POST and URL http://localhost:8080/game/admin/create-admin.
Go to the Body tab, select raw, and choose JSON as the format.
Provide the new admin details in JSON format, e.g.:
jsonCopy code{
  "username": "newadmin",
  "password": "password",
  "email": "newadmin@example.com"
}

Go to the Params tab and add a key-value pair with the key username and the authenticated admin's username as the value.
Click Send. You should receive a 200 OK response with the newly created admin details.


Create Employee

Follow the same steps as above, but use the URL http://localhost:8080/game/admin/create-employee and provide employee details in the request body.


Delete Player and Score

Create a new request with method DELETE and URL http://localhost:8080/game/admin/scores/{id}, where {id} is the ID of the score you want to delete.
Go to the Params tab and add a key-value pair with the key username and the authenticated admin's username as the value.
Click Send. You should receive a 200 OK response with the message "Player and score deleted successfully".



3. Common Operations

Get Top Scores

Create a new request with method GET and URL http://localhost:8080/game/topscores.
You can optionally add a query parameter n to specify the number of top scores to retrieve, e.g., http://localhost:8080/game/topscores?n=10.
Click Send. You should receive a 200 OK response with the top scores.


Add a New Score

Create a new request with method POST and URL http://localhost:8080/game/player.
Go to the Body tab, select raw, and choose JSON as the format.
Provide the player name and score in JSON format, e.g.:
jsonCopy code{
  "playerName": "John",
  "score": 85
}

Go to the Params tab and add a key-value pair with the key username and the authenticated admin's or employee's username as the value.
Click Send. You should receive a 200 OK response.


Get Scores with Pagination

Create a new request with method GET and URL http://localhost:8080/game/scores.
You can optionally add query parameters page and size to specify the page number and page size, e.g., http://localhost:8080/game/scores?page=1&size=10.
Click Send. You should receive a 200 OK response with the scores and pagination links.


Update a Score

Create a new request with method PUT and URL http://localhost:8080/game/scores/{id}, where {id} is the ID of the score you want to update.
Go to the Params tab and add two key-value pairs:

newScore: The new score value
username: The authenticated admin's or employee's username


Click Send. You should receive a 200 OK response with the message "Score updated successfully".


Get a Specific Score by ID

Create a new request with method GET and URL http://localhost:8080/game/scores/{id}, where {id} is the ID of the score you want to retrieve.
Click Send. You should receive a 200 OK response with the score details.


Logout

Create a new request with method POST and URL http://localhost:8080/game/logout.
Go to the Params tab and add a key-value pair with the key username and the authenticated admin's or employee's username as the value.
Click Send. You should receive a 200 OK response with the message "Logged out successfully".



Make sure to replace the placeholders (e.g., {id}) with the appropriate values and adjust the URLs if you're running the application on a different host or port.
