resource "exoscale_security_group" "clojurians_log_v2" {
  name        = "clojurians_log"
  description = "Security Group for Clojurians-log"
}

resource "exoscale_security_group_rule" "clojurians_log_v2_ping" {
  security_group_id = exoscale_security_group.clojurians_log_v2.id
  description       = "Ping"
  type              = "INGRESS"
  protocol          = "ICMP"
  icmp_type         = 8
  icmp_code         = 0
  cidr              = "0.0.0.0/0"
}

resource "exoscale_security_group_rule" "clojurians_log_v2_ping6" {
  security_group_id = exoscale_security_group.clojurians_log_v2.id
  description       = "Ping6"
  type              = "INGRESS"
  protocol          = "ICMPv6"
  icmp_type         = 128
  icmp_code         = 0
  cidr              = "::/0"
}

resource "exoscale_security_group_rule" "clojurians_log_v2_tcp" {
  for_each = toset(["22", "80", "443"])

  security_group_id = exoscale_security_group.clojurians_log_v2.id
  type              = "INGRESS"
  start_port        = each.value
  end_port          = each.value
  protocol          = "TCP"
  cidr              = "0.0.0.0/0"
}

resource "exoscale_security_group_rule" "clojurians_log_v2_tcp6" {
  for_each = toset(["22", "80", "443"])

  security_group_id = exoscale_security_group.clojurians_log_v2.id
  type              = "INGRESS"
  start_port        = each.value
  end_port          = each.value
  protocol          = "TCP"
  cidr              = "::/0"
}
