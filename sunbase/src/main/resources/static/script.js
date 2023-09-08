const baseUrl= "http://localhost:8080/";
async function verifyLogin(){

    const loginId= document.getElementById("loginId").value;
    const password=document.getElementById("password").value;
    if(loginId.trim()==="" || password.trim()===""){
    alert("Login Id or Password should not be empty");
     return false;
    }
    const response = await fetch(baseUrl+"login",{
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                login_id: loginId,
                password: password
            })
    });
    if(response.ok){
        document.getElementById("loginPage").style.display="none";
        document.getElementById("addorview").style.display="block";
    }
    else{
        alert("Please check your credentials");
         return false;
    }
}
async function addCustomer(){
     const firstName= document.getElementById("firstName").value;
     const lastName=document.getElementById("lastName").value;
     const street=document.getElementById("street").value;
     const address=document.getElementById("address").value;
     const city=document.getElementById("city").value;
     const state=document.getElementById("state").value;
     const email=document.getElementById("email").value;
     const phone=document.getElementById("phone").value;

     if (firstName.trim() === "" || lastName.trim() === "") {
        alert("First name and last name should not be empty");
        return false;
     }
     const response = await fetch(baseUrl+"create",{
                   method: "POST",
                   headers: {
                       "Content-Type": "application/json"
                   },
                   body: JSON.stringify({
                       first_name: firstName,
                       last_name: lastName,
                       street: street,
                       address: address,
                       city: city,
                       state: state,
                       email: email,
                       phone: phone
                   })
               });
          if (response.status === 201) {
               alert("Customer created successfully.");
               document.getElementById("addForm").style.display="none";
               document.getElementById("msg").innerHTML="Added successfully. Click Show Customers to view";
          } else if (response.status === 400) {
              alert("First Name or Last Name is missing.");
          } else {
                      alert("Failed to create customer.");
                  }
}
function showAddCustomerScreen() {
    document.getElementById("loginPage").style.display = "none";
    document.getElementById("customerList").style.display = "none";
    document.getElementById("addForm").style.display = "block";
}
async function getCustomerDetails(){
   document.getElementById("customerList").style.display = "block";
   document.getElementById("customerTable").style.display = "block";
   document.getElementById("addForm").style.display = "none";
   document.getElementById("msg").style.display = "none";

   const response = await fetch(baseUrl+"customerList");
   const customers = await response.json();
   clearTable();
   const tbody = document.querySelector("tbody");
   customers.forEach(customer => {
                   const row = document.createElement("tr");
                   row.setAttribute("data-id", `${customer.uuid}`);
                   row.innerHTML = `
                       <td id="first_name">${customer.first_name}</td>
                       <td id="last_name">${customer.last_name}</td>
                       <td id="street">${customer.street}</td>
                       <td id="address"> ${customer.address}</td>
                       <td id="city">${customer.city}</td>
                       <td id="state">${customer.state}</td>
                       <td id="email">${customer.email}</td>
                       <td id="phone">${customer.phone}</td>
                       <td>
                       <div class="action-icons">
                       <div class="fas fa-edit edit-icon" data-id="${customer.uuid}" onclick="editRow('${customer.uuid}')"></div>
                       <div class="fas fa-trash delete-icon" data-id="${customer.uuid}" onclick="deleteCustomer('${customer.uuid}')"></div>
                       </div>
                       </td>
                   `;

                   tbody.appendChild(row);
               });

}
function deleteCustomer(uuidToDelete){
    const confirmDelete = confirm('Are you sure you want to delete this customer?');
    if(confirmDelete){
        fetchDeleteCustomer(uuidToDelete);
    }
}
async function fetchDeleteCustomer(uuidToDelete){
    const response = await fetch(`${baseUrl}delete?uuid=${uuidToDelete}`,{ method: "POST"});
    if (response.status === 200) {
       console.log('Successfully deleted');
     } else if (response.status === 500) {
       console.error('Error: Not deleted due to a server error');
     } else if (response.status === 400) {
       response.json().then(data => {
         if (data.error === 'UUID not found') {
           console.error('Error: UUID not found');
         } else {
           console.error('Error: Unknown client error');
         }
       });
     } else {
       console.error('Error: Unknown error');
     }
    getCustomerDetails();

}
function clearTable() {
    const tableBody = document.querySelector("tbody");
    tableBody.innerHTML = "";
}

function editRow(id) {
    const row = document.querySelector(`tr[data-id="${id}"]`);
    const cells = row.querySelectorAll('td');
    for (let i = 0; i < cells.length - 1; i++) {
        const cell = cells[i];
        const cellText = cell.textContent.trim();
        const input = document.createElement('input');
        input.type = 'text';
        input.id = cell.getAttribute('id');
        input.value = cellText;
        input.style.width = '100%';
        cell.textContent = '';
        cell.appendChild(input);
    }
    const actionCell = row.querySelector('td:last-child');
    actionCell.innerHTML = `
        <div class="action-icons">
        <div class="fas fa-save save-icon" data-id="${id}" onclick="saveRow('${id}')"></div>
         <div class="fas fa-times-circle cancel-icon" data-id="${id}" onclick="cancelEdit('${id}')"></div>
         </div>
    `;
}

function saveRow(id) {
    const row = document.querySelector(`tr[data-id="${id}"]`);
    const inputs = row.querySelectorAll('input');
    const data = {};
    for (let i =0; i < inputs.length; i++) {
        const input = inputs[i];
        const fieldName = inputs[i].getAttribute('id');
        const inputValue = input.value;
        data[fieldName] = inputValue;
    }
    updateCustomer(data,id);
    const actionCell = row.querySelector('td:last-child');
    actionCell.innerHTML = ` <div class="fas fa-edit edit-icon" data-id="${id}" onclick="editRow('${id}')"></div>`;
}

function cancelEdit(id) {
    getCustomerDetails();

}

async function updateCustomer(customer,id){

    const response = await fetch(`${baseUrl}update?uuid=${id}`,{
                                                   method: "POST",
                                                   headers: {
                                                       "Content-Type": "application/json"
                                                   },
                                                   body: JSON.stringify(customer)
                                               });


  if (response.status === 200) {
       console.log('Successfully Updated');
     } else if (response.status === 500) {
       console.error('Error: Not updated due to a server error');
     } else if (response.status === 400) {
       response.json().then(data => {
         if (data.error === 'UUID not found') {
           console.error('Error: UUID not found');
         } else {
           console.error('Error: Unknown client error');
         }
       });
     } else {
       console.error('Error: Unknown error');
     }
    getCustomerDetails();
}