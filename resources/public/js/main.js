var btn = document.getElementById('mobile-menu-btn')
var sidebar = document.getElementById('sidebar')

btn.addEventListener("click", function() {
    sidebar.classList.toggle("-translate-x-full")
})
