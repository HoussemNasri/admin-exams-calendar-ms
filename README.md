# Exam Calendar Rest API
## API
**GET** `/api/examens`

> Fetch all calenders information in JSON format.

**GET** `/api/examens/{id}`

> Fetch calender information of `id` in JSON format.

**GET** `/api/examens/uploads/{id}`

> Download calender document file of `id`.

**POST** `/api/examens`

> Upload `file` to the server and create a new entry in the database    .

- **Body:**
    - **file** the pdf file to upload
    - **semester** The semester that calender belongs to `{FIRST, SECOND}`
    - **session** The session that calender belongs to `{PRINCIPALE, RATTRAPAGE, COVID}`
 
**PUT** `/api/examens/{id}`

> Rename the calendar fileName

- **Body:**
    - **name** The new filename

**PUT** `/api/examens/uploads/{id}`

> Update the document file for calender of `{id}` 

- **Body:**
    - **file** the new pdf file
   
