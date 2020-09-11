# AstraPortia
Demo project fo Stargate Hackathon

We did stuff and now you get to read stuff. The general idea here was to create an application that leveraged new capabiltites for Astra and the Stargate API for Astra. Given the climate lately we thought it would be a good idea to find other planets we could live on and help others do the same while also keeping track of any near Earth objects that might decide to end our existence on our wonderful planet.

This application is implemented with Java using Spring Boot and Maven. Commands issued will reflect this.

Please note that you can run this application using GitPod or locally. The GitPod button will appear in the top right corner if you are logged into GitHub.

Be sure to check out our [FRICTION DOC](https://docs.google.com/document/d/1jbm4oqW8sTexEaGnskca2Lohs8uiZIvtCcqyrnz4BMs/edit?usp=sharing) to see what the experience was like for our team.

## 1. Astra Organizations
Since we needed to collaborate using a single database it seemed like a good idea to try out the new Organizations feature. With a single org we all had a single, shared database to use. Nice!

![Clavis Org](https://user-images.githubusercontent.com/23346205/92964820-c21e3580-f442-11ea-8448-34927ca50ee7.png)

## 2. Data Model and Data Loading
Ok, so we had a team database, great. Now we needed a data model and data to work with for our application. We took two approaches. One used DSBulk and the secure connect bundle with a [CSV](./1_datasets/PSCompPars_2020.09.10_11.00.24.csv?raw=true) file. The other used the new Stargate API via a test harness within our java application to create the data model and import the dataset via [JSON](./1_datasets/2020_09_10_near_earth_asteroids_and_comets.json?raw=true).

### DSBulk
Planetary systems composite dataset

**✅ Step 2a. Create data model**
In this case, we created our data model first by creating the planetary_systems_composite.cql file located [HERE](./2_import_script) and executing those within the CQLSH of Astra. Just paste the contents of the [planetary_systems_composite.cql](./2_import_script/planetary_systems_composite.cql?raw=true) into the CQLSH console after logging in and setting the context of your database to **USE** the **_stargate_** keyspace.

![CQLSH console](https://user-images.githubusercontent.com/23346205/92967285-f693f080-f446-11ea-9048-a249d89ba9b0.png)

**✅ Step 2b. Import data into the database**
Take note of the use of the option ```-b "secure-connect-clavis.zip"``` that allows us to connect to our Astra database pretty seamlessly using DSBulk. You can download the secure bundle for your Astra database by using the **Download secure connect bundle** link in the **Connection Details** section of Astra. **NOTE** that your database name will most likely be different than ours.

![Secure connect bundle](https://user-images.githubusercontent.com/23346205/92966392-728d3900-f445-11ea-9af6-23531f293a8e.png)


_In order for this command to work you will need to [install DSBulk](https://downloads.datastax.com/#bulk-loader) if you have not done so already. Once installed, ensure to either set the PATH for the location of dsbulk or simply run dsbulk from the /bin dir of your installation directory._


📘 **Command to execute**
```
dsbulk load -url 1_datasets/PSCompPars_2020.09.10_11.00.24.csv -k stargate -t planetary_systems_composite -b "secure-connect-clavis.zip" -u gateuser -p gatepassword -header false -m '0=pl_name,1=hostname,2=sy_snum,3=sy_pnum,4=discoverymethod,5=disc_year,6=disc_facility'
```

📗 **Expected output**
Once you run the above command you should see something that looks similar to the following.

![DSBulk output](https://user-images.githubusercontent.com/23346205/92966132-f4309700-f444-11ea-9e3f-2954138fa03a.png)

### JSON load using Stargate API
Near Earth Object dataset

**✅ Step 2c. Create data model and import dataset**
In this next case we used the Stargate API to both create the data model and load the dataset via a test harness within our java application.

Whether you are using GitPod or a local environment ensure to navigate to the **astra-portia-api** directory from the root **AstraPortia**. This is where our Spring Boot application lives.

📘 **Command to execute**
```
cd astra-portia-api
pwd
```

📗 **Expected output**

![PWD](https://user-images.githubusercontent.com/23346205/92967919-14ae2080-f448-11ea-9aa7-9ae06ac3722e.png)

_Note the the absolute path will show your username, not the one you see in the image from my instance. The important thing is that you are located in the **AstraPortia/astra-portia-api** directory._

**✅ Step 2d. Execute the test to create the data model and import data**
This step will use the a JSON file embedded in the application located [HERE](./astra-portia-api/src/main/resources/2020_09_10_near_earth_asteroids_and_comets.json?raw=true)

📘 **Command to execute**
```
mvn test
```

📗 **Expected output**

![mvn test](https://user-images.githubusercontent.com/23346205/92968352-f3016900-f448-11ea-817b-d5c31ad0b4e4.png)

## 3. The Application
Ok, now we have our data model and data. The next step is to run the application and see what it does. Since we are using Spring Boot this is really quite simple.

**✅ Step 3a. Run the app**

📘 **Command to execute**
```
mvn spring-boot:run
```

📗 **Expected output**

![spring boot](https://user-images.githubusercontent.com/23346205/92968584-6c00c080-f449-11ea-957a-f92ea3658969.png)

**✅ Step 3b. Go to your browser**

The application is configured to run at port **8081** so if you are running **locally** navigate to **_localhost:8081_** in your browser. If you are running via **GitPod** a dialog will give you the option to **Run in Browser** once you launch the app.

![Portia Astria](https://user-images.githubusercontent.com/23346205/92968947-0103b980-f44a-11ea-964d-e68c4912c864.png)


**✅ Step 3c. Have fun traveling and exploring**

Once there notice there are a set of tabs at the top of the interface.

![tabs](https://user-images.githubusercontent.com/23346205/92969138-6788d780-f44a-11ea-9eea-833e17aec007.png)

While at the **Stargate** be sure to click on all of the chevrons to activate the portal. 

Use the **Earth Catalog** to look at the very, very small list of items we have in there right now.

Finally, the **API Docs** will bring you to a swagger interface you can use to browse the back end API we implemented to work with the Stargate API. This was done to abstract the API a bit and make it very easy to start getting data from the database.
