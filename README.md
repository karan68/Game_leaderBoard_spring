For running JWT :

Go to postman --> POST(http://localhost:8090/authenticate) with Body :
{
    "username": "foolishuser",
    "password": "foolishuser"
}

This will send a request to get JWT token and then you can use it in any of your API hits by using it in 
Headers:
Key: Authorization, Value: Bearer <token> (Replace <token> with the JWT token received from the authentication request)
