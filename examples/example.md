## JSON-to-XLS

JSON-to-XLS is a webservice that allows you to create an XLS template
and then fill it dynamically with JSON data.

This service leverages the excellent [jXLS](http://jxls.sourceforge.net/) library.

### Web service

A working version of the JSON-to-XLS is at:

http://xls.ona.io

### API

* **POST /templates** with excel template file as payload.
    This saves the template and returns a unique token for that template in the response. Henceforth this token can be used for referring this template.
* **POST /xls/<template_token>** with JSON data as payload.
    This will use the template corresponding to the token in the URL and generate an Excel file using the template and JSON data. The URL using which this excel file can be downloaded is returned in the response.
* **GET /xls/<generated_excel_token>**
    This returns the excel file for given token.

### Template

The [school-example.xls](school-example.xls) provides a simple example of an XLS
template.

![](https://www.evernote.com/shard/s2/sh/a9ad92f8-3dbf-4a9b-b290-3ce4a81914a1/edb72035d554c9092af82afbb6091ef6/deep/0/Screenshot-3-16-14,-11-46-AM.png)

Top level element in the template must be ${data}.

You can use the power of excel functions in your template. Just make sure to wrap it with $[...].

ex) =A1+B2 becomes $[A1+B1]

### Data (JSON)

```json
{
    "district": "District A",
    "schools": [
        {
            "name": "School A",
            "males": 40,
            "females": 50
        },
        {
            "name": "School B",
            "males": 56,
            "females": 45
        },
        {
            "name": "School C",
            "males": 34,
            "females": 63
        }
    ]
}

```

### Usage Example

1. Download the [Postman](http://www.getpostman.com/) chrome extension
2. Upload the XLS template.
   * Set the server setting to http://xls.ona.io/templates
   * Click binary and upload the [school-example.xls](school-example.xls) template
   * Click send

   ![](https://www.evernote.com/shard/s2/sh/3d4ba590-d4f3-4eb2-856b-fc2d70540eb8/67391c070740623bcb42c17f91388654/res/0e7843da-d558-4a8e-9044-1d101a90a8bd/skitch.png)

3. After posting it will return the xls template token. Which you will use next when you post the JSON data.

   ![](https://www.evernote.com/shard/s2/sh/f2d488e2-272f-47ae-a728-96b5951b3c0a/cf9c21d85f535a302f240e8f23209396/deep/0/Postman.png)  

4. Post the JSON file [schools.json](schools.json) to http://xls.ona.io/xls/[template-token]

   ![](https://www.evernote.com/shard/s2/sh/d7a40539-0622-4555-bbea-bd6f072d13bc/734e4049807d6768334bfc36ae2213e9/deep/0/Postman-and-Postman.png)

5. It will return a link the filled out xls file which you can download via a GET call or by copying and pasting the URL in your browser.

   Ex: [http://xls.ona.io/xls/a4f071c7860841fa9a87b1b8d001c81c](http://xls.ona.io/xls/a4f071c7860841fa9a87b1b8d001c81c)

   ![](https://www.evernote.com/shard/s2/sh/d1077cdd-15e0-4fb1-80b4-0a07695636fc/a22d4275dfc514303ab6bf33341c0881/deep/0/Postman.png)

6. Congrats! You have a dynamically filled out XLS file!

   [school-example-response.xls](school-example-response.xls)
