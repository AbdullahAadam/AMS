$(document).ready(function () {
    // Fetch CSRF token
    $.get("/csrf", function(data) {
        window.csrfToken = data.token;
        window.csrfHeader = data.headerName;
        console.log("CSRF Token retrieved:", window.csrfToken);
    });

    // Menu item click event
    $(".menu-item").click(function(){
        var url = $(this).attr('data-href');
        window.location.href = url;
    });

    // Submenu toggle functionality
    $(".submenu").on("click", function() {
        let submenuIcon = $(this).find(".images");
        $(".submenu").not(this).removeClass("active").find(".images").attr("src", "/images/admin/arrow2.png");
        $(this).toggleClass("active");
        submenuIcon.attr("src", $(this).hasClass("active") ? "/images/admin/arrow1.png" : "/images/admin/arrow2.png");
    });
	let subCode = $(".subCode strong").text().trim();
	let semNo = $(".semNo strong").text().trim();
	let markingDate = $(".header2 span:nth-child(1) span").text().trim();
	let period = $(".header2 span:nth-child(4) strong").text().trim();
	// 🔥 Fetch previously marked attendance when the page loads
	$.ajax({
		type: "GET",
	    url: "/professor/get-attendance",
	    data: { subId: subCode, semNo: semNo, attDate: markingDate, period: period },
	    success: function (response) {
		    response.forEach(function (attendance) {
				let row = $("tr:has(.regNo:contains(" + attendance.regNo + "))");
		        let button = row.find(".status-btn[data-status='" + attendance.status + "']");
		        row.find(".status-btn").removeClass("selected"); // Remove highlight
		        button.addClass("selected"); // Highlight saved status
		    });
	     }
	});

	$(".status-btn").click(function (){
		//alert("Button clicked!");
		let button=$(this);
		let row = button.closest("tr");
		let regNo = button.data("regno");
		let subCode = $(".subCode strong").text().trim();
		let semNo = $(".semNo strong").text().trim();
		let status = button.data("status");
		let markingDate = $(".header2 span:nth-child(1) span").text().trim();
		let period = $(".header2 span:nth-child(4) strong").text().trim();
		let role=$("#role").text().trim();
		let batch=$("#batch").text().trim();
		let profId=$("#profId").text().trim();
		console.log("Button disabled: ", button.prop("disabled"));
		if (button.attr("disabled")) {  
		        alert("Attendance is locked and cannot be modified.");
		        return;
		}
		$.ajax({
			type: "POST",
		    url: "/professor/mark-attendanced",
		    contentType: "application/json",
			xhrFields:{
				withCredentials: true
			},
			headers: { [window.csrfHeader]: window.csrfToken },
		    data: JSON.stringify({
			    regNo: regNo,
			    subId: subCode,
			    attDate: markingDate,
			    period: period,
			    status: status,
			    semNo: semNo,
				batch:batch,
				markedBy:role,
				markedByUser:profId
			}),
		    success: function (response) {
		                    // 🔥 Remove highlight from all buttons in this row
			   row.find(".status-btn").removeClass("selected");
			                    // 🔥 Highlight the clicked button
			   button.addClass("selected");
			   //alert("Attendance updated: " + status);
		   },
		   error: function (xhr) {
		       alert("Error: " + xhr.responseText);
			   console.log("Error: " + xhr.responseText);
		   }
		});
	});
	
	
	/*$('.status-btn').click(function () {
	    $(this).siblings().removeClass('selected'); // Remove selection from siblings
	    $(this).addClass('selected'); // Add selection to clicked button
	});*/
});