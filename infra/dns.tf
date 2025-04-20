resource "cloudflare_record" "clojureverse_slack-event-sink_A" {
  name    = "slack-event-sink"
  value   = exoscale_compute_instance.clojurians-log-v2.public_ip_address
  type    = "A"
  ttl     = 60
  proxied = false
  zone_id = "8c9a72d8fd6c3044f216e4c9b598b0b8"
}
