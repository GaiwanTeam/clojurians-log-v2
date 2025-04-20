locals {
  # Exoscale will run this on instance creation
  cloudinit = templatefile("cloudinit.yml", {})
}

data "exoscale_template" "debian-12" {
  zone = "de-muc-1"
  name = "Linux Debian 12 (Bookworm) 64-bit"
}

resource "exoscale_compute_instance" "clojurians-log-v2" {
  name        = "clojurians-log-v2"
  template_id = data.exoscale_template.debian-12.id
  zone        = "de-muc-1"
  type        = "standard.small"
  disk_size   = 400
  ssh_key     = var.exoscale_ssh_keypair_name
  user_data   = local.cloudinit

  security_group_ids = [exoscale_security_group.clojurians_log_v2.id]

  labels = {
  }
}
