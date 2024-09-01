data "terraform_remote_state" "infra" {
  backend = "s3"
  config = {
    bucket = "es2-vadebicicleta-terraform-states"
    region = "us-east-1"
    key    = "apps/alb.tfstate"
  }
}

resource "aws_eip_association" "my_eip_association" {
  instance_id = aws_instance.example_app.id
  allocation_id = data.terraform_remote_state.infra.outputs.eip_externo_id ### MUDA EXAMPLE PARA O NOME DO SEU MICROSERVICO
}

output "app_ip2" {
  value = aws_eip_association.my_eip_association.public_ip
}