$(document).ready(function () {
    // Menu toggle logic
    $(".menu-item").click(function () {
        var url = $(this).attr('data-href');
        window.location.href = url;
    });

    $(".submenu").on("click", function () {
        let submenuIcon = $(this).find(".images");
        $(".submenu").not(this).removeClass("active").find(".images").attr("src", "/images/admin/arrow2.png");
        $(this).toggleClass("active");
        submenuIcon.attr("src", $(this).hasClass("active") ? "/images/admin/arrow1.png" : "/images/admin/arrow2.png");
    });
	
});