terraform {
  required_version = ">=1.2.0"
  required_providers { aws = { version = ">=4.30.0" } }
  backend "s3" {
    bucket = "es2-vadebicicleta-terraform-states"
    region = "us-east-1"
    acl = "bucket-owner-full-control"
    key = "apps/ecs-app-example.tfstate"
  }
}