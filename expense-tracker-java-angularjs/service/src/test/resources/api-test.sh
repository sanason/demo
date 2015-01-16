######### Happy path ##########

# Create user
curl -d '{"username":"bart.nason", "password":"password"}' -H "Content-Type: application/json" -i -k -X POST https://localhost:8443/expense-tracker-service/users

# Authenticate user
curl -d '{"username":"bart.nason", "password":"password"}' -H "Content-Type: application/json" -i -k -X POST https://localhost:8443/expense-tracker-service/authenticate

# Add expense
curl -d '{"date":"2014-12-20T00:00:00.000Z","time":"0000-00-00T13:25:00.000Z","description":"Amazon.com","amount":20.32,"comment":"Christmas present for Mom"}' \
     -H "Content-Type: application/json" -i -k \
     -H "Authorization: token eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyIiwiZXhwIjoxNDIwNTg2ODMyfQ.QITMshs-Ke6ceLNWTFaKpK-M_vgfJQhCxpNWhYNG3SE" \
     -X POST https://localhost:8443/expense-tracker-service/users/2/expenses

# Add expense
curl -d '{"date":"2014-12-18T00:00:00.000Z","time":"0000-00-00T13:25:00.000Z","description":"Target","amount":100.01,"comment":"Toys"}' \
     -H "Content-Type: application/json" -i -k \
     -H "Authorization: token eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyIiwiZXhwIjoxNDIwNTMzODkxfQ.6sU9NV-TvgFWCkK5q9IGdWzwLIKBcipLk6mNvSEg79k" \
     -X POST https://localhost:8443/expense-tracker-service/users/2/expenses

# All expenses
curl -i -k "https://localhost:8443/expense-tracker-service/users/2/expenses" \
     -H "Authorization: token eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyIiwiZXhwIjoxNDIwNTMzODkxfQ.6sU9NV-TvgFWCkK5q9IGdWzwLIKBcipLk6mNvSEg79k" \
     -H "Accept: application/json"

# Filter expenses
curl -i -k "https://localhost:8443/expense-tracker-service/users/2/expenses?date=after:2014-12-16&time=before:20:56&description=Target&amount=moreThan:90.33&comment=toy" \
     -H "Authorization: token eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyIiwiZXhwIjoxNDIwNTMzODkxfQ.6sU9NV-TvgFWCkK5q9IGdWzwLIKBcipLk6mNvSEg79k" \
     -H "Accept: application/json"

# Modify expense
curl -d '{"date":"2014-12-18T00:00:00.000Z","time":"0000-00-00T13:25:00.000Z","description":"Target","amount":90.01,"comment":"Toys"}' \
     -H "Content-Type: application/json" -i -k \
     -H "Authorization: token eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyIiwiZXhwIjoxNDIwNTMzODkxfQ.6sU9NV-TvgFWCkK5q9IGdWzwLIKBcipLk6mNvSEg79k" \
     -X PUT "https://localhost:8443/expense-tracker-service/users/2/expenses/2"

# Aggregate expenses
curl "https://localhost:8443/expense-tracker-service/users/2/expenses/aggregates?ftn=sum&field=amount&grouping=week" \
     -H "Authorization: token eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyIiwiZXhwIjoxNDIwNTMzODkxfQ.6sU9NV-TvgFWCkK5q9IGdWzwLIKBcipLk6mNvSEg79k" \
     -H "Accept: application/json" -i -k

# Delete expense
curl -X DELETE https://localhost:8443/expense-tracker-service/users/2/expenses/21 \
     -i -k -H "Authorization: token eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyIiwiZXhwIjoxNDIwNTMzODkxfQ.6sU9NV-TvgFWCkK5q9IGdWzwLIKBcipLk6mNvSEg79k"

# Test 1
curl -d '{"username":"shelley", "password":"abc123aa"}' -H "Content-Type: application/json" -i -X POST http://localhost:8080/expense-tracker/api/users

# Test 2
curl -i -H "Accept: application/json" -X GET http://localhost:8080/expense-tracker/api/users/1/expenses

# Test 3
curl -i -H "Accept: application/json" -X GET https://localhost:8443/expense-tracker/api/users/1/expenses -tlsv1 -k

# Test 4
curl -i -H "Accept: application/json" -H "Authorization: token unsignedToken" -X GET https://localhost:8443/expense-tracker/api/users/1/expenses -tlsv1 -k