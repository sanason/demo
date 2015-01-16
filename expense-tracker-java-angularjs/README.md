### Expense Tracker

A basic webapp for demo purposes.
 
#### Build

Requires JDK 1.7 or higher.

To build, run in the root directory:

```
mvn package
```

This will create two WAR files:
* Under /app, a WAR containing the front-end webapp
* Under /service, a WAR containing the back-end API service

#### Deploy

Must run in a web server accepting SSL connections on port 8443.

### REST API
#### Authentication

All API URLs require SSL.

URLs of the following form require an authentication token:

```
/users/*/expenses/**
```

The token should be included in the request headers:

```
Authorization:token <token>
```

To obtain an authentication token:

```
POST /authenticate
 ```
 
Request body

* username (string) - **Required**
* password (string) - **Required**

```
{
  "username" : "shelley.nason",
  "password" : "password"
}
```
Response

```
Status: 200 OK
{
  "token" : "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNDIwNDAwMTU3fQ.vWDAmcrGaQynRnphn1VSuMFyMjUpolQR3VsSRgWny3Y",
  "expiration" : 1420400157824,
  "user" : {
    "id" : 1,
    "username" : "shelley.nason:"
  }
}
```
Test

```
curl -d '{"username":"shelley.nason", "password":"password"}' -H "Content-Type: application/json" -i -X POST https://localhost:8443/expense-tracker-service/authenticate
```
#### Users
##### List users
```
GET /users
```
Response

```
Status: 200 OK
[
  {
    "id": 1,
    "username": "shelley.nason"
  }
]
```
Test

```
curl -H "Accept: application/json" -i https://localhost:8443/expense-tracker-service/users
```

##### Get a single user (by id or username)
```
GET /users/:user
```
Response

```
Status: 200 OK
{
  "id": 1,
  "username": "shelley.nason"
}
```
Test

```
curl -H "Accept: application/json" -i https://localhost:8443/expense-tracker-service/users/1
curl -H "Accept: application/json" -i http://localhost:8443/expense-tracker-service/users/shelley.nason
```

##### Add a user
```
POST /users
```

Request body

* username (string) - **Required** Between 1 and 20 characters. Must be unique.
* password (string) - **Required** Between 8 and 100 characters.

```
{
  "username" : "shelley.nason",
  "password" : "password"
}
```
Response

```
Status: 201 Created
{
  "id" : 1,
  "username" : "shelley.nason"
}
```
Test

```
curl -d '{"username":"shelley.nason", "password":"password"}' -H "Content-Type: application/json" -i -X POST https://localhost:8443/expense-tracker-service/users
```

#### Expenses
##### List expenses for user
```
GET /users/:user/expenses
```
Parameters

* date (string) - **Optional** In the format before:2014-12-20+after:2014-12-18
* time (string) - **Optional** In the format before:18:33+after:20:56
* description (string) - **Optional** Partial case-insensitive match to description field
* amount (string) - **Optional** In the format lessThan:10.00+moreThan:20.00
* comment (string) - **Optional** Partial case-insensitive match to comment field

```
?date=after:2014-12-16&time=before:20:56&description=Target&amount=moreThan:100.33&comment=toy
```
Response

```
Status: 200 OK
[
  {
    "id" : 1,
    "date" : "2014-12-20T00:00:00Z",
    "time" : "0000-00-00T13:25:00Z",
    "description" : "Amazon.com",
    "amount" : 20.33,
    "comment" : "Christmas present for Mom"
  }
]
```
Test

```
curl https://localhost:8443/expense-tracker-service/users/1/expenses?date=before:2014-12-20&time=after:20:56&description=QFC&amount=moreThan:23.33&comment=food \
     -H "Authorization: token eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNDIwNDAwMTU3fQ.vWDAmcrGaQynRnphn1VSuMFyMjUpolQR3VsSRgWny3Y" \
     -H "Accept: application/json" -i
```

##### Add new expense for user
```
POST /users/:user/expenses
```
Request body

* date (string) - **Required** Timestamp in the format YYYY-MM-DDT00:00:00Z.
* time (string) - **Optional** Timestamp in the format 0000-00-00THH:MM:SSZ
* description (string) - **Optional** Max 50 characters.
* amount (number) - **Required** Decimal number matching '[0-9]{0,10}\.[0-9]{0,2}'.
* description (string) - **Optional** Max 500 characters.

```
{
    "date" : "2014-12-20T00:00:00.000Z",
    "time" : "0000-00-00T13:25:00.000Z",
    "description" : "Amazon.com",
    "amount" : 20.33,
    "comment" : "Christmas present for Mom"
}
```
Response

```
Status: 201 Created
{
  "id" : 1,
  "date" : "2014-12-20T00:00:00.000Z",
  "time" : "0000-00-00T13:25:00.000Z",
  "description" : "Amazon.com",
  "amount" : 20.33,
  "comment" : "Christmas present for Mom"
}
```
Test

```
curl -d '{"date":"2014-12-20T00:00:00.000Z","time":"0000-00-00T13:25:00.000Z","description":"Amazon.com","amount":20.32,"comment":"Christmas present for Mom"}' \
     -H "Content-Type: application/json" -i \
     -H "Authorization: token eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNDIwNDAwMTU3fQ.vWDAmcrGaQynRnphn1VSuMFyMjUpolQR3VsSRgWny3Y" \
     -X POST https://localhost:8443/expense-tracker-service/users/1/expenses
```

##### Modify existing expense for user
```
PUT /users/:user/expenses/:expense
```

Request body

See documentation for POST.


Response

Same as for POST but with Status: 200 OK.

Test

```
curl -d '{"date":"2014-12-20T00:00:00.000Z","time":"0000-00-00T13:25:00.000Z","description":"Amazon.com","amount":20.32,"comment":"Christmas present for Mom"}' \
     -H "Content-Type: application/json" -i \
     -H "Authorization: token eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNDIwNDAwMTU3fQ.vWDAmcrGaQynRnphn1VSuMFyMjUpolQR3VsSRgWny3Y" \
     -X PUT https://localhost:8443/expense-tracker-service/users/1/expenses/1

```

##### Delete existing expense for user

```
DELETE /users/:user/expenses/:expense
```
Response

```
Status: 204 No Content
```

Test

```
curl -X DELETE https://localhost:8443/expense-tracker-service/users/1/expenses/1 \
     -i -H "Authorization: token eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNDIwNDAwMTU3fQ.vWDAmcrGaQynRnphn1VSuMFyMjUpolQR3VsSRgWny3Y"
```

##### Aggregates

```
GET /users/:userId/expenses/aggregates
```

Parameters

* ftn (string) - **Required** Constant - one of {sum, avg, min, max} (only sum is implemented)
* field (string) - **Required** Constant - one of {amount}
* grouping (string) - **Required** Constant - one of {day, week, month, year} (only week is implemented)

```
?ftn=sum&field=amount&grouping=week
```
Response

```
Status: 200 OK
[
  {
    "key" : "12/21/2014",
    "sum_amount" : "45.32"
  },
  {
    "key" : "12/14/2014",
    "sum_amount" : "23.33"
  }
]
```
Test

```
curl https://localhost:8443/expense-tracker-service/users/1/expenses/aggregates?ftn=sum&field=amount&grouping=week \
     -H "Authorization: token eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNDIwNDAwMTU3fQ.vWDAmcrGaQynRnphn1VSuMFyMjUpolQR3VsSRgWny3Y" \
     -H "Accept: application/json" -i
```
#### Client Errors
Invalid JSON

```
Status: 400 Bad Request
```

Invalid fields

```
Status: 422 Unprocessable Entity
{
  "message": "Validation Failed",
  "errors": [
    {
      "resource": "User",
      "field": "username",
      "message": "Username must be unique."
    }
  ]
}
```