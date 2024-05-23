#! /bin/sh
sudo yum update -y
sudo yum install docker -y
sudo service docker start
sudo usermod -a -G docker ec2-user
sudo chkconfig docker on

echo "$DOCKER_PASSWORD" | sudo docker login -u "$DOCKER_USERNAME" --password-stdin

sudo docker run -d -p 8080:8080 fabriciobcv/es2-grupoc-vadebicicleta-base-example
