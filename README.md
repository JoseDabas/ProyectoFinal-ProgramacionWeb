URL_MONGO="mongodb+srv://josearieldabas01:HL4OcEYAGqynX5Jj@josedatabase.7dkjm.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"
DB_NOMBRE="proyecto_final"

Script y comandos para AWS:

wget https://raw.githubusercontent.com/JoseDabas/virtualhost-proyecto-final/refs/heads/main/basico-ubuntu.sh && chmod +x basico-ubuntu.sh && bash basico-ubuntu.sh

cd /etc/apache2/sites-available/

sudo a2enmod proxy proxy_html  proxy_wstunnel proxy_http rewrite

sudo systemctl restart apache2

sudo service apache2 stop

sudo certbot certonly -m jamd0001@ce.pucmm.edu.do -d shorturl.turnos.do

sudo nano seguro.conf 

sudo a2ensite seguro.conf

sudo a2enmod ssl

sudo a2enmod ssl proxy proxy_http headers

sudo apache2ctl configtest

sudo systemctl restart apache2

java -jar app/build/libs/app.jar > output.log 2>&1 &

tail -f output.log


