# Banking API

Money Transfer API.

## Requirements 
Install [Docker](https://docs.docker.com/v17.09/engine/installation/)

## Usage

#### Run
Use make build, test and run the project.

```bash
make run
```
#### Create two Accounts

```bash
curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"account_holder": "Ibra","currency_and_balance": "EUR 20.5"}' \
  localhost:8080/accounts/create
```
#### List the accounts you created 
```bash
curl localhost:8080/accounts/list
```
This will return a response like the following.
```json
[
    {
        "account_holder": "ibra",
        "account_number": "bc8f927f-250e-4180-9449-0aafd1bbbfd1",
        "currency_and_balance": "EUR 10000000.50"
    },
    {
        "account_holder": "ibra",
        "account_number": "a4997092-4601-4464-8bbb-e53e1c80b43c",
        "currency_and_balance": "EUR 10000000.50"
    }
]
```

#### Transfer Money
Using the accounts number you get previously form the following command.

```bash
curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"sender_account_number": "bc8f927f-250e-4180-9449-0aafd1bbbfd1", "beneficiary_account_number": "a4997092-4601-4464-8bbb-e53e1c80b43c", "currency_and_amount": "EUR 3"}' \
  localhost:8080/payments/send
```

you're done, you'll get response similar to.
```json
{
  "message":"Transaction Done Successfully!",
  "current-balance":"EUR 9999997.50",
  "timestamp":"2019-12-01T21:46:47.630202177"
}
```