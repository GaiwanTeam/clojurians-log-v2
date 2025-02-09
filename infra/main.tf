################################################################################
# Backend setup where OpenTofu state is stored.
# This is on Exoscale, but we treat it as if it's S3.

# If you need to (re-)init the backend, make sure to pass
# "-backend-config=access_key=... -backend-config=secret_key=...", as done by
# bin/lambdev tf init

terraform {
  required_version = ">= 1.8.5"

  backend "s3" {
    bucket = "clojurians-log-tfstate"
    key    = "tfstates/lambdaisland.tfstate"
    # Dummy for S3 compat.
    region = "de-fra-1"
    endpoints = {
      s3 = "https://sos-de-fra-1.exo.io"
    }

    skip_credentials_validation = true
    skip_requesting_account_id  = true
    skip_metadata_api_check     = true
    skip_region_validation      = true
    skip_s3_checksum            = true
    use_path_style              = true
  }

  required_providers {
    cloudflare = {
      source  = "cloudflare/cloudflare"
      version = "2.26.0"
    }

    dnsimple = {
      source  = "dnsimple/dnsimple"
      version = "0.5.3"
    }

    exoscale = {
      source  = "exoscale/exoscale"
      version = "0.62.3"
    }
  }
}

################################################################################
# Variables, these are taken from the environment.
#
# TF_VAR_exoscale_ssh_keypair_name="Arne (bmo)"
# TF_VAR_exoscale_api_key="EXO..."
# TF_VAR_exoscale_secret_key="..."

variable "exoscale_api_key" {
  default     = "<read from env>"
  type        = string
  description = "The API key from Exoscale"
}

variable "exoscale_secret_key" {
  default     = "<read from env>"
  type        = string
  description = "The Secret Key from Exoscale"
}

variable "exoscale_ssh_keypair_name" {
  default     = "<read from env>"
  type        = string
  description = "The SSH keypair to be allowed on the instance"
}

variable "dnsimple_token" {
  default     = "<read from env>"
  type        = string
  description = "The SSH keypair to be allowed on the instance"
}

variable "cloudflare_email" {
  default     = "<read from env>"
  type        = string
  description = "The cloudflare account email address"
}

variable "cloudflare_api_token" {
  default     = "<read from env>"
  type        = string
  description = "The cloudflare api token"
}

################################################################################
# Providers for the resources we will manage

provider "exoscale" {
  key    = var.exoscale_api_key
  secret = var.exoscale_secret_key
}

provider "dnsimple" {
  token   = var.dnsimple_token
  account = "28031"
}

provider "cloudflare" {
  email     = var.cloudflare_email
  api_token = var.cloudflare_api_token
}
