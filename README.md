USMedicine-Interactort  App

App Description: 

- USMedicine-Interactort App is an android mobile application, which helps the user to save the drugs as list with interaction details(Drug which has the interaction with the another drug added in the list) using the phone’s camera for scanning the medication’s name/Barcode/QR code.


List Menu:

- User should have two options to add the medication(Drug) name in the application,

- User can scan the drug’s name/Barcode/QR Code using the device camera(ML Text-recognition/barcode/QRCode recognition) and save the information in the application

- User can search the drug’s name  via “Search” option and add the drug to the list inside the application
   

Interaction Menu:

- User can add multiple drug’s name in the list. The drug tile will be denoted with bolded text with an exclamation mark, if there is an another drug in the list that interacts with it

- User can view the details of the drug, which interacts with another drug in the list by the selection of the interaction-enabled item


Scan Menu:

-	User can scan the drug name/Barcode/QR code from the mobile camera and add the drug to the list

-	If in case of Barcode/QRCode scan, the app should convert the Barcode/QR code data to RXCUI code and add the relevant drug to the list


Search Menu:

-	User can search for the drug name by entering the query in the query field. Which provide the suggestions when the drug name is miss-spelled.

-	Once the user selects the suggestion given, the drug’s name will be displayed to the user with an 'add' button to add that specific drug to the list of their added drugs


Edit Menu:

-	User can edit the list by deleting the drug which has been added by the user

====================================================================================================================================================================================

To view the code base:

- Extract the zip folder shared to open/view the project folder of the application

- Please navigate to,
- app/src/main/java/com/druginteractr,
- to view all the project folders used in the application


To view the list of API's used:

- Please navigate to, 
- app/src/main/java/com/druginteractr/data/network/DrugAPI.kt
- Base URL can be viewed in, 
- app/src/main/java/com/druginteractr/utils/Constants.kt


To view the Room DB details:

- Please navigate to,
- app/src/main/java/com/druginteractr/room
- to view the Roon DB related details


To view the MLKIT- Text/BarCode/QR Code implementations: 

- Please navigate to,
- app/src/main/java/com/druginteractr/mlkit
- to view the MLKIT implementation


To view the test cases:

- Please navigate to,
- app/src/androidTest/java/com/druginteractr
- to view the test cases used in the application


To view the used libraries/dependencies: 

- Please navigate to,
- app/build.gradle


To view the resources folder: 

- Please navigate to,
- app/src/main/res
- to view all the resources used in the application


To view the UI design layouts: 

- Please navigate to,
- app/src/main/res/layout
- to view all the UI layouts used in the application
