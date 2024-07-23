#! /bin/sh
sudo yum update -y
sudo yum install docker -y
sudo service docker start
sudo usermod -a -G docker ec2-user
sudo chkconfig docker on

echo "$DOCKER_PASSWORD" | sudo docker login -u "$DOCKER_USERNAME" --password-stdin

sudo docker run -d -e MAIL_USERNAME=${MAIL_USERNAME} -e MAIL_PASSWORD=${MAIL_PASSWORD} -e URL_OPERADORA=${URL_OPERADORA} -e OPERADORA_ID=${OPERADORA_ID} -e OPERADORA_KEY=${OPERADORA_KEY} -p 8080:8080 fabriciobcv/es2-grupoc-vadebicicleta-externo
