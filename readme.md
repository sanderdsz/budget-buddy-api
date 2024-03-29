# Budget Buddy

> A simple home finance application to manage and control, yours and your family, budget.

This project aims to create a complete application which manages the income's and spending's flux.

### Authorization and Authentication

## Internal Flow

- [x] Access and refresh tokens creation.
- [x] POST user details persistence into MySQL.
- [x] POST access token persistence into Redis.
- [x] Tokens revalidation and persistence.
- [x] Tokens expire verification.
- [x] POST avatar file blob upload.
- [x] PUT avatar file.
- [x] GET user details.
- [x] GET token verification and validation.
- [x] Retrieve the connection accepted and requested into a single list.
- [x] Accept a new connection between users.
- [x] Cancel a connection between users.

## GitHub Flow

Using GitHub APP installation for the authorization flow, because it's the only way that returns
a refresh_token, when the OAUTH flow implement's it, should be wise to change the provider from APP to OAUTH.

- [ ] Redirect auth URL.
- [ ] Access and refresh tokens creation.
- [ ] User details persistence into MySQL.
- [ ] Access token persistence into Redis.
- [ ] Tokens revalidation and persistence.
- [ ] Tokens expire verification.

## Google Flow

- [ ] Redirect auth URL. (Done at frontend)
- [ ] Google access tokens creation. (Done at frontend)
- [x] User details persistence into MySQL.
- [x] Access token persistence into Redis.
- [x] Internal tokens revalidation and persistence.
- [x] Google access tokens expire verification.

### Expenses

- [x] POST new expense.
- [x] PUT expense.
- [x] GET monthly expenses summary.
- [x] GET all expenses.
- [x] Expenses date and type filters.
- [x] Expenses pagination.
- [x] GET sum expenses current month.
- [x] GET sum expenses current year.
- [x] GET sum expenses monthly by year.

### Incomes

- [x] POST new income.
- [x] PUT income.
- [x] GET all incomes.
- [x] Income date and type filters.
- [x] Incomes pagination.

### Balances

- [x] GET balance by period.
- [x] GET balance by date and year.
- [x] GET balance by weeks.
- [x] GET balance by monthly.

### Investments

- [ ] POST new investment.
- [ ] PUT investment.
- [ ] GET annual investment.
- [ ] GET monthly investment.

### Withdraws

- [ ] POST new withdraw.
- [ ] PUT withdraw.
- [ ] GET annual withdraw.
- [ ] GET monthly withdraw.

### Import

- [x] Endpoint to import xlsx file for expenses and incomes.

### Login Chart

![Login Chart](.github/budget-buddy-login.png)