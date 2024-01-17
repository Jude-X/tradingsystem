# Getting Started
To Build project:

``
./gradlew build -x test
``

To run tests:
``
./gradlew test
``

To start app:
``
./gradlew bootRun
``


## Trading System
Postman collection can be found here: [Trading System.postman_collection.json](Trading%20System.postman_collection.json)

### Assumptions made:
- Composite Instruments are traded as a whole, 
though they comprise of underlying instruments as described in the instrument class,
for simplicity, I matched them once the instrumentId on the buy side is same on the sell side

### Technical decision
- Made use of strategy pattern to allow alternating between multiple matching algorithm
- Matching is done using task scheduler that runs every second, running the active algorithm
- Mocked a `quote/candle service` by fluctuating the price by ±1 in the instrument service class
- Made use of concurrent maps to handle concurrent updates.
