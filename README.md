# Transfer service
The service provides functionality to transfer money between two bank accounts. The functionality to create bank 
accounts is not supported.

# Testing
Three types of tests are implemented.
* Unit - Tests the components in isolation with mocked dependencies   
* MVC - Tests controllers using MockMvc. Tests the setup of all the used MVC elements working together as intended.
* Feature - Tests high-level gherkin statements executed against the full service. 

The feature tests are located in src/featureTest. The tests attempt to follow the given criteria as closely as possible 
with only a few wording changes and one dropped statement that did not seem to make sense (it's left in but commented 
out).

You may notice that I like to write my unit tests in a style that mimics gherkin. While tests written in this style 
require additional thought and effort to setup the testing 'framework' for the given unit, I believe that they are more 
readable and maintainable compared to how most people I've seen write their tests.  

# Improvements
## Database
A separate persistent database should be added (eg. MariaDb) instead of the in-memory H2.
This can be achieved by adding the relevant driver to the build.gradle and setting the appropriate application 
properties. This can be then tested using testcontainers in the featureTests. I have had connection issues while 
writing these tests and due to the lack of time have eventually decided to drop these changes.  

## Validation
Javax validation annotations can be used to validate the entities instead of programmatic validation in the service 
layer. However because the produced validation messages would not be caught by the RestErrorHandler, refactoring of 
error handling is required for this to work with the predefined messages and statuses.  

## Documentation
The API as it stands is not well documented. The controller should've been annotated with the Swagger annotations and
the documentation exposed on the designated endpoint. 

## Correlation ID and logging
At the minute the service provides very limited logging and for the logging it does there is no correlation.
A correlation id interceptor can be added to read the "X-Correlation-Id" header (or generate a new one) and use that in 
the log messages.

## Auditing
The database entities implement no auditing as it stands. The basic auditing can be implemented using the JPA callback 
methods on the entities adding fields like created_at or updated_at.

## Health and metrics
The spring boot actuator can be added to monitor the usage of the API as well as to provide access to healthchecks.
The monitoring could be implemented using an aspect intercepting the controller methods, marking the invocations using 
micrometer and export the results to a persistent store (eg. InfluxDb).
The health of the service as is depends only on whether or not the service itself is up (as it uses an embedded db). 
This could be extended to monitor connection to a separate persistent db once added.

## Currency conversion
At the minute only transactions between accounts in the same currency are supported. This can be changed by implementing 
a conversion mechanism. The mechanism I was thinking of was to call out for the conversion rate to a separate service 
(eg. https://openexchangerates.org/). Depending on how up-to-date the information would need to be, the results would 
be stored in a cache. In case of unavailability the cached value would be used. In case of unavailability from the 
start-up, the calls would fail (or a default conversion table could be used).

## Non-long entity ids
The ids of the entities being just numeric and auto-increasing inherently exposes more information than intended. These 
ids could be changed to be UUID to prevent that. 