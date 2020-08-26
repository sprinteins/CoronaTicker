# CoronaTicker

CoronaTicker is a small tool to display corona statistics (cases per 100.000 for the last 7 days. for certain districts) in Microsoft Teams.

Based on the rapid prototype principle, the initial version of this took less than 10 minutes to implement.

## Installation

1. Clone the repository.
2. Adjust the [RKI REST API URL](https://npgeo-corona-npgeo-de.hub.arcgis.com/datasets/917fc37a709542548cc3be077a786c17_0/geoservice) and choose the 'Landkreise' you wish to be notified about.
3. Adjust your locations ('Standorte') to your own. 
4. Change Microsoft Teams Webhook URL to your own.

## Build and Run

```bash
gradle clean build fatCapsule eclipse
java -jar build/libs/CoronaTicker-capsule.jar 

```

## License
[MIT](https://choosealicense.com/licenses/mit/)