let lat, lon;

window.onload = function() {
    console.log("geolocalizacion.js loaded"); // This will log when the script is loaded

    if (navigator.geolocation) {
        console.log("Geolocation API is supported"); // This will log if the Geolocation API is supported

        navigator.geolocation.getCurrentPosition(function(position) {
            document.getElementById('latitud').value = position.coords.latitude;
            document.getElementById('longitud').value = position.coords.longitude;
            // Success callback
            lat = position.coords.latitude;
            lon = position.coords.longitude;

            console.log("Location retrieved: ", lat, lon); // This will log the retrieved location
        }, function(error) {
            // Error callback
            console.log("Error retrieving location: ", error); // This will log any errors
        });
    } else {
        console.log("Geolocation is not supported by this browser.");
    }
}

function setCoordinates() {
    // Store the coordinates in the form's hidden input fields
    document.getElementById('latitud').value = lat;
    document.getElementById('longitud').value = lon;
}