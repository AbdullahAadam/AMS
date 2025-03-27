$(document).ready(function(){
	$.get("/csrf", function(data) {
		// data might look like: { parameterName: "_csrf", token: "abc123", headerName: "X-CSRF-TOKEN" }
		window.csrfToken = data.token;
		window.csrfHeader = data.headerName;
		console.log("CSRF Token retrieved:", window.csrfToken);
	});
	$(".menu-item").click(function(){
		var url=$(this).attr('data-href');
		window.location.href=url;
	});
	$(".submenu").on("click", function() {
		let submenuIcon = $(this).find(".images");
		$(".submenu").not(this).removeClass("active").find(".images").attr("src", "/images/admin/arrow2.png");
		$(this).toggleClass("active");
		submenuIcon.attr("src", $(this).hasClass("active") ? "/images/admin/arrow1.png" : "/images/admin/arrow2.png");
	});
	var totalYears = $("#yearFilter").data("total-years"); // Get total years from data attribute

	   if (!totalYears) {
	       console.error("Total years not found.");
	       return;
	   }

	   var yearDropdown = $("#yearFilter");

	   for (var i = 1; i < totalYears; i++) {
	       var yearText = convertToRoman(i); // Convert number to Roman numeral
	       yearDropdown.append(`<option value="${yearText}">Year ${yearText}</option>`);
	   }

	   var finalYearText = convertToRoman(totalYears);
	   yearDropdown.append(`<option value="FINAL">Year ${finalYearText}</option>`);
	function filterStudents() {
	       let searchValue = $("#studentSearch").val().toLowerCase();
	       let selectedYear = $("#yearFilter").val();

	       $(".student-card").each(function () {
	           let name = $(this).find(".student-name").text().toLowerCase();
	           let regNo = $(this).find(".reg-no").text().toLowerCase();
	           let year = $(this).attr("data-year");

	           let matchesSearch = name.includes(searchValue) || regNo.includes(searchValue);
	           let matchesYear = selectedYear === "all" || year === selectedYear;

	           $(this).toggle(matchesSearch && matchesYear);
	       });
	}
	function convertToRoman(num) {
	      var roman = ["I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"];
	      return num <= roman.length ? roman[num - 1] : num;
	}
	$("#studentSearch").on("input", filterStudents);
	$("#yearFilter").on("change", filterStudents);
});