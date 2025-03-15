let db;

function initializeDB() {
    return new Promise((resolve, reject) => {
        let request = window.indexedDB.open("EncuestasDB", 1);

        request.onerror = function() {
            console.log('Database failed to open');
            reject(new Error('Database failed to open'));
        };

        request.onsuccess = function() {
            console.log('Database opened successfully');
            db = request.result;
            resolve(db);
        };

        request.onupgradeneeded = function(e) {
            let db = e.target.result;
            let objectStore = db.createObjectStore('encuestas', { keyPath: 'id', autoIncrement: true });
            objectStore.createIndex('nombre', 'nombre', { unique: false });
            objectStore.createIndex('sector', 'sector', { unique: false });
            objectStore.createIndex('nivelEscolar', 'nivelEscolar', { unique: false });
            objectStore.createIndex('latitud', 'latitud', { unique: false });
            objectStore.createIndex('longitud', 'longitud', { unique: false });
            objectStore.createIndex('usuario', 'usuario', { unique: true });
            console.log('Database setup complete');
        };
    });
}

window.initializeDB = initializeDB;