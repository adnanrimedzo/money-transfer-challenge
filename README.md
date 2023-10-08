# Dev Challenge

### Changes
I upgraded the spring project with Java17, springboot 3.2. also i set the port from 18080 t0 8080.

New endpoint added for being able to make money transfer.
the endpoint: `POST http://lovalhost:8080/v1/money-transfer`
the body: 
```json
{
  "transactionId" :  "123",
  "sourceAccountId" : "1",
  "targetAccountId" : "2",
  "amount" : 10.00
}
```
`transactionId` is for avoiding recurrent transaction. It is expected from the source of request. 
`sourceAccountId` is the account from.
`targetAccountId` is the account to.
`amount` is the transfer amount. the minimum amount is `0.01`

New integration tests added for having high test coverage and being able to test all flow instead of doing blackbox testing.

### The changes which are not added due to not spending more than one hour for the test case

- Log4j configuration
- swagger and api documentation
- tests for repositories
- async call or circuit breaker for notification service
- currency for the balances.
- domain driven design with hexagonal architecture
