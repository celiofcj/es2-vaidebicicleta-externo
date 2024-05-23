variable "DOCKER_USERNAME" {default = ""}
variable "DOCKER_PASSWORD" {default = ""}
variable "commit" {default = ""}

locals{
  app_name = "example_app"
  app_port = 8080
  instace_type = "t2.micro"
  device_name = "/dev/sdf"
  vol_size = 20
  commit = var.commit
  app_image_url = "fabriciobcv/es2-grupoc-vadebicicleta-externo"
  docker_username = var.DOCKER_USERNAME
  docker_password = var.DOCKER_PASSWORD
}