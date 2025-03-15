// 1. Función para obtener todos los registros de la IndexedDB
function getAllRecords() {
    return new Promise((resolve, reject) => {
        let transaction = db.transaction(['encuestas'], 'readonly');
        let objectStore = transaction.objectStore('encuestas');
        let request = objectStore.getAll();
        request.onsuccess = function () {
            console.log("Registros obtenidos:", request.result);
            resolve(request.result);
        };
        request.onerror = function () {
            console.error("Error al obtener registros:", request.error);
            reject(request.error);
        };
    });
}

// 2. Función para mostrar los registros en una tabla HTML
async function displayRecords() {
    try {
        db = await window.initializeDB();
        let records = await getAllRecords();
        let table = document.getElementById('recordsTable');
        let tbody = table.getElementsByTagName('tbody')[0];
        tbody.innerHTML = ''; // Limpiar la tabla antes de agregar nuevos registros
        
        records.forEach(record => {
            let row = tbody.insertRow();
            row.insertCell().innerText = record.nombre || '';
            row.insertCell().innerText = record.sector || '';
            row.insertCell().innerText = record.nivelEscolar || '';
            
            let actionsCell = row.insertCell();
            
            // Crear botón de editar con clases de Bootstrap y Font Awesome
            let editButton = document.createElement('button');
            editButton.className = 'btn btn-sm btn-primary mr-2';
            editButton.innerHTML = '<i class="fas fa-edit mr-1"></i>Editar';
            editButton.onclick = function() {
                editRecord(record.id);
            };
            actionsCell.appendChild(editButton);
            
            // Crear botón de borrar con clases de Bootstrap y Font Awesome
            let deleteButton = document.createElement('button');
            deleteButton.className = 'btn btn-sm btn-danger';
            deleteButton.innerHTML = '<i class="fas fa-trash-alt mr-1"></i>Borrar';
            deleteButton.onclick = function() {
                deleteRecord(record.id);
            };
            actionsCell.appendChild(deleteButton);
        });
        
        // Configurar los event listeners para los botones
        setupEventListeners();
    } catch (error) {
        console.error("Error en displayRecords:", error);
    }
}

// Función para configurar los event listeners
function setupEventListeners() {
    // Event listener para guardar cambios en el registro
    document.getElementById('saveButton').addEventListener('click', function() {
        let id = Number(document.getElementById('editId').value);
        let nombre = document.getElementById('editNombre').value;
        let sector = document.getElementById('editSector').value;
        let nivelEscolar = document.getElementById('editNivelEscolar').value;
        let usuario = document.getElementById('editUsuario').value;

        let transaction = db.transaction(['encuestas'], 'readwrite');
        let objectStore = transaction.objectStore('encuestas');
        let request = objectStore.get(id);
        request.onsuccess = function () {
            let record = request.result;
            if (record) {
                record.nombre = nombre;
                record.sector = sector;
                record.nivelEscolar = nivelEscolar;
                record.usuario = usuario;

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

    // Event listener para confirmar borrado
    document.getElementById('confirmDeleteButton').addEventListener('click', function() {
        let id = Number(this.dataset.recordId);
        if (!isNaN(id)) {
            let transaction = db.transaction(['encuestas'], 'readwrite');
            let objectStore = transaction.objectStore('encuestas');
            let request = objectStore.delete(id);
            request.onsuccess = function () {
                console.log('Registro borrado con éxito');
                location.reload();
            };
            request.onerror = function () {
                console.log('Error al borrar el registro');
            };
            $('#deleteModal').modal('hide');
        } else {
            console.log('El id del registro a borrar no es un número válido');
        }
    });

    // Event listener para el botón de sincronización
    let syncButton = document.getElementById('syncButton');
    if (syncButton) {
        syncButton.onclick = function () {
            syncWithServer();
        };
    }
}

// 3. Funciones para manejar las acciones de editar y borrar
function editRecord(id) {
    let transaction = db.transaction(['encuestas'], 'readonly');
    let objectStore = transaction.objectStore('encuestas');
    let request = objectStore.get(id);
    request.onsuccess = function () {
        let record = request.result;
        if (record) {
            document.getElementById('editId').value = record.id;
            document.getElementById('editNombre').value = record.nombre || '';
            document.getElementById('editSector').value = record.sector || '';
            document.getElementById('editNivelEscolar').value = record.nivelEscolar || '';
            document.getElementById('editUsuario').value = record.usuario || '';
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
    let confirmDeleteButton = document.getElementById('confirmDeleteButton');
    if (confirmDeleteButton) {
        confirmDeleteButton.dataset.recordId = id;
        $('#deleteModal').modal('show');
    } else {
        console.log('No se encontró el botón de confirmación de borrado');
    }
}

// 4. Funciones para sincronizar con el servidor
var webSocket;

async function syncWithServer() {
    let records = await getAllRecords();
    console.log("Registros a sincronizar:", records);
    
    if (records && records.length > 0) {
        verificarConexion(records);
    } else {
        console.log("No hay registros para sincronizar");
        alert("No hay registros para sincronizar");
    }
}

function conectar(records) {
    //Cambiado a wss
    webSocket = new WebSocket("wss://" + location.hostname + ":" + location.port + "/encuesta/sincronizar");
    webSocket.onopen = function() {
        webSocket.send(JSON.stringify(records));
        deleteAllRecords();
    };
    webSocket.onerror = function(error) {
        console.log('WebSocket Error: ', error);
    };
    webSocket.onclose = function() {
        console.log("Desconectado - status " + this.readyState);
    };
}

function deleteAllRecords() {
    let transaction = db.transaction(['encuestas'], 'readwrite');
    let objectStore = transaction.objectStore('encuestas');
    let request = objectStore.clear();
    request.onsuccess = function () {
        console.log('Todos los registros han sido borrados de la IndexedDB');
        location.reload();
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