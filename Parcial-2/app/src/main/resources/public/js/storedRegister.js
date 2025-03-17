// 1. Crear una función para obtener todos los registros de la IndexedDB.
function getAllRecords() {
    return new Promise((resolve, reject) => {
        let transaction = db.transaction(['encuestas'], 'readonly');
        let objectStore = transaction.objectStore('encuestas');
        let request = objectStore.getAll();
        request.onsuccess = function () {
            resolve(request.result);
        };
        request.onerror = function () {
            reject(request.error);
        };
    });
}

// 2. Crear una función para mostrar los registros en una tabla HTML.
async function displayRecords() {
    await window.initializeDB();
    let records = await getAllRecords();
    let table = document.getElementById('recordsTable');
    records.forEach(record => {
        let row = table.insertRow();
        row.insertCell().innerText = record.nombre;
        row.insertCell().innerText = record.sector;
        row.insertCell().innerText = record.nivelEscolar;
        let editButton = document.createElement('button');
        let deleteButton = document.createElement('button');
        if (editButton && deleteButton) { // Check that the buttons are not null
            editButton.innerText = 'Editar';
            editButton.onclick = function () {
                editRecord(record.id);
            };
            row.insertCell().appendChild(editButton);
            deleteButton.innerText = 'Borrar';
            deleteButton.onclick = function () {
                deleteRecord(record.id);
            };
            row.insertCell().appendChild(deleteButton);
        }
    });

    document.getElementById('saveButton').addEventListener('click', function() {
        let id = Number(document.getElementById('editId').value); // Asegúrate de que el id es un número
        let nombre = document.getElementById('editNombre').value;
        let sector = document.getElementById('editSector').value;
        let nivelEscolar = document.getElementById('editNivelEscolar').value;
        let usuario = document.getElementById('editUsuario').value;

        let transaction = db.transaction(['encuestas'], 'readwrite');
        let objectStore = transaction.objectStore('encuestas');
        let request = objectStore.get(id);
        request.onsuccess = function () {
            let record = request.result;
            if (record) { // Comprobar que record no es undefined
                // Actualizar los valores del registro
                record.nombre = nombre;
                record.sector = sector;
                record.nivelEscolar = nivelEscolar;
                record.usuario = usuario;

                // Guardar el registro actualizado en la base de datos
                let updateRequest = objectStore.put(record);
                updateRequest.onsuccess = function () {
                    console.log('Registro actualizado con éxito');
                    location.reload();
                };
                updateRequest.onerror = function () {
                    console.log('Error al actualizar el registro');
                };

                $('#editModal').modal('hide');
            } else {
                console.log('No se encontró ningún registro con el id: ', id);
            }
        };
    });

    document.getElementById('confirmDeleteButton').addEventListener('click', function() {
        // Obtener el id del registro a borrar del atributo de datos del botón de confirmación
        let id = Number(this.dataset.recordId);
        if (!isNaN(id)) { // Comprobar que id es un número
            let transaction = db.transaction(['encuestas'], 'readwrite');
            let objectStore = transaction.objectStore('encuestas');
            let request = objectStore.delete(id);
            request.onsuccess = function () {
                console.log('Registro borrado con éxito');
                location.reload(); // Refrescar la página para mostrar los cambios
            };
            request.onerror = function () {
                console.log('Error al borrar el registro');
            };
            // Ocultar el modal de confirmación
            $('#deleteModal').modal('hide');
        } else {
            console.log('El id del registro a borrar no es un número válido');
        }
    });
}

// 3. Crear funciones para manejar las acciones de editar y borrar.
function editRecord(id) {
    let transaction = db.transaction(['encuestas'], 'readonly');
    let objectStore = transaction.objectStore('encuestas');
    let request = objectStore.get(id);
    request.onsuccess = function () {
        let record = request.result;
        if (record) { // Comprobar que record no es undefined
            document.getElementById('editId').value = record.id;
            document.getElementById('editNombre').value = record.nombre;
            document.getElementById('editSector').value = record.sector;
            document.getElementById('editNivelEscolar').value = record.nivelEscolar;
            document.getElementById('editUsuario').value = record.usuario;
            $('#editModal').modal('show');
        } else {
            console.log('No se encontró ningún registro con el id: ', id);
        }
    };
}

function deleteRecord(id) {
    if (isNaN(id)) {
        console.log('El id del registro a borrar no es un número válido');
        return;
    }
    // Guardar el id del registro a borrar en un atributo de datos del botón de confirmación
    let confirmDeleteButton = document.getElementById('confirmDeleteButton');
    if (confirmDeleteButton) {
        confirmDeleteButton.dataset.recordId = id;
        $('#deleteModal').modal('show');
    } else {
        console.log('No se encontró el botón de confirmación de borrado');
    }
}

// 4. Crear un botón para sincronizar los datos con el servidor.
let syncButton = document.getElementById('syncButton');
syncButton.onclick = function () {
    syncWithServer();
};

var webSocket;
//var tiempoReconectar = 5000;

async function syncWithServer() {
    let records = await getAllRecords();
    verificarConexion(records);
    /*$.ajax({
        url: '/encuesta/sincronizar',
        type: 'POST',
        data: JSON.stringify(records),
        contentType: 'application/json',
        success: function(response) {
            console.log('Sincronización exitosa');
            deleteAllRecords();
        },
        error: function(error) {
            console.log('Error en la sincronización', error);
        }
    });*/
}

function conectar(records) {
    webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/encuesta/sincronizar");
    webSocket.onopen = function() {
        webSocket.send(JSON.stringify(records));
        deleteAllRecords();
    };
    webSocket.onerror = function(error) {
        console.log('WebSocket Error: ', error);
    };
    webSocket.onclose = function() {
        console.log("Desconectado - status "+this.readyState);
    };
}

function deleteAllRecords() {
    let transaction = db.transaction(['encuestas'], 'readwrite');
    let objectStore = transaction.objectStore('encuestas');
    let request = objectStore.clear();
    request.onsuccess = function () {
        console.log('Todos los registros han sido borrados de la IndexedDB');
        location.reload()
    };
    request.onerror = function () {
        console.log('Error al borrar los registros de la IndexedDB');
    };
}

function verificarConexion(records){
    if(!webSocket || webSocket.readyState == 3){
        conectar(records);
    }
}
// Exponer la función displayRecords en el objeto window
window.displayRecords = displayRecords;

