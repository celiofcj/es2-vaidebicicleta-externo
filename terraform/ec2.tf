data "aws_ami" "amzn-linux-2023-ami" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-2023.*-x86_64"]
  }
}

resource "aws_security_group" "example_sg" {
  name = "${local.app_name}-sg"
  description = "Allow all trafic in 8080 port and all outbound traffic"
  tags = {
    Name = "${local.app_name}-sg"
  }
}

resource "aws_vpc_security_group_ingress_rule" "ingress_rule" {
  security_group_id = aws_security_group.example_sg.id
  cidr_ipv4         = "0.0.0.0/0"
  from_port = 8080
  to_port = 8080
  ip_protocol = "tcp"
}

resource "aws_vpc_security_group_egress_rule" "egress_rule" {
  security_group_id = aws_security_group.example_sg.id
  cidr_ipv4         = "0.0.0.0/0"
  ip_protocol       = "-1" 
}

resource "aws_launch_template" "example_app_lt" {
  name = local.app_name
  image_id = data.aws_ami.amzn-linux-2023-ami.id
  instance_type = local.instace_type

  vpc_security_group_ids = [aws_security_group.example_sg.id]

  user_data = base64encode(templatefile("files/install-and-run-docker.sh.tpl", {
    DOCKER_USERNAME = local.docker_username
    DOCKER_PASSWORD = local.docker_password
    APP_IMAGE_URL = local.app_image_url
    COMMIT = local.commit
  }))
}

resource "aws_instance" "example_app" {
  key_name = "ec2_access"
  launch_template {
    id = aws_launch_template.example_app_lt.id
  }

  lifecycle {
    create_before_destroy = true
  }
}

output "app_dns" { value = aws_instance.example_app.public_dns }
output "app_ip" { value = aws_instance.example_app.public_ip }
output "app_image" { value = "${local.app_image_url}:${local.commit}"}

