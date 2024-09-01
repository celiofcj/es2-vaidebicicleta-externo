#! /bin/sh
sudo yum update -y
sudo yum install docker -y
sudo service docker start
sudo usermod -a -G docker ec2-user
sudo chkconfig docker on

echo "$DOCKER_PASSWORD" | sudo docker login -u "$DOCKER_USERNAME" --password-stdin

sudo docker run -d -e MAIL_USERNAME=${MAIL_USERNAME} -e MAIL_PASSWORD="${MAIL_PASSWORD}" -e OPERADORA_URL=${OPERADORA_URL} -e OPERADORA_ID=${OPERADORA_ID} -e OPERADORA_KEY=${OPERADORA_KEY} -e ALUGUEL_URL=${ALUGUEL_URL} -p 8080:8080 fabriciobcv/es2-grupoc-vadebicicleta-externo
