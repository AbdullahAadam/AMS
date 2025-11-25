$(document).ready(function(){
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
	$(".view-all-btn").click(function(){			
		window.location.href="/student/attendance";
	});
	
	$.ajax({
	   url: '/student/attendance-status',
	   method: 'GET',
	   success: function (data) {
	     const total = parseInt($("#totalPeriods").text().trim());
	     let markedCount = 0;
	     let html = "";

	     for (let i = 1; i <= total; i++) {
	       const statusRaw = data[i]; // e.g., "PRESENT", "ABSENT", "LATE", "OD"
	       let statusClass = "not-marked";

	       if (statusRaw) {
	         markedCount++;
	         switch (statusRaw.toUpperCase()) {
	           case "PRESENT":
	             statusClass = "present";
	             break;
	           case "ABSENT":
	             statusClass = "absent";
	             break;
	           case "LATE":
	             statusClass = "late";
	             break;
	           case "OD":
	             statusClass = "od";
	             break;
	           default:
	             statusClass = "not-marked";
	         }
	       }

	       html += `<div class="hour ${statusClass}" title="${statusRaw || 'Not Marked'}">${i} hr</div>`;
	     }

	     $('#hoursContainer').html(html);
	     $('#markedCount').text(markedCount);
	   },
	   error: function () {
	     alert("Could not load today's attendance.");
	   }
	 });
	 let regNo=$("#regNo").text().trim();
	 let sem=$("#currentSem").text().trim();
	 let semNo=sem.split(" ")[1];
	 console.log("regNo:",regNo);
	 console.log("semNo:",semNo);
	 $.ajax({
	    url: `/student/attendance/total/`,
	    method: 'GET',
	    success: function (response) {
	      const percentage = Math.round(response.totalAttendancePercentage);
	      const total = response.totalPeriods;
	      const present = response.totalPresentPeriods + response.totalODPeriods;
		  const startDate = response.startDate || "N/A";
		  const endDate = response.endDate || "N/A";
		  $("#sem-dates").html(`<span>${startDate}</span> - <span>${endDate}</span>`);

	      // Determine color based on % range
	      let color = "#f44336"; // red by default
	      if (percentage >= 80) {
	        color = "#4caf50"; // green
	      } else if (percentage >= 60) {
	        color = "#ff9800"; // orange
	      }

	      // Update UI
	      $('#total-hours').text(total);
	      $('#present-hours').text(present);
	      $('#attendance-circle')
	        .css({
	          '--p': percentage,
	          '--c': color
	        })
	        .text(`${percentage}%`);
	    },
	    error: function (xhr) {
	      console.error("Error fetching attendance:", xhr.responseJSON?.error || "Unknown error");
	      alert("Failed to load attendance data.");
	    }
	  });
	  $.ajax({
	                 url: '/student/upcoming',
	                 method: 'GET',
	                 success: function(holidays) {
	                     if (holidays.length === 0) {
	                         $('#cardBody').html('<div class="holiday-slide active"><div class="card-body-left"><h2>No Holidays</h2><p>No holidays in the next 30 days</p></div></div>');
	                         return;
	                     }
	                     
	                     // Create slides
	                     holidays.forEach((holiday, index) => {
	                         const slide = createSlide(holiday, index === 0);
	                         $('#cardBody').append(slide);
	                     });
	                     
	                     // Only show controls if multiple holidays
	                     if (holidays.length > 1) {
	                         $('#cardBody').append(`
	                             <div class="controls">
	                                 <button id="prevBtn"><i class="fas fa-chevron-left"></i></button>
	                                 <button id="nextBtn"><i class="fas fa-chevron-right"></i></button>
	                             </div>
	                         `);
	                         setupNavigation(holidays);
	                     }
	                 },
	                 error: function() {
	                     $('#cardBody').html('<div class="holiday-slide active"><div class="card-body-left"><h2>Error</h2><p>Could not load holidays</p></div></div>');
	                 }
	             });
	             
	             function createSlide(holiday, isActive) {
	                 const isSunday = holiday.holidayName === "Sunday";
	                 const formattedDate = new Date(holiday.holidayDate).toLocaleDateString('en-US', 
	                     { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });
	                 
	                 return $(`
	                     <div class="holiday-slide ${isActive ? 'active' : 'next'}">
	                         <div class="card-body-left">
	                             ${isSunday ? '<div class="sunday-badge">Sunday</div>' : ''}
	                             <h2>${holiday.holidayName}</h2>
	                             <p><i class="far fa-calendar-alt"></i> ${formattedDate}</p>
	                             <p><i class="fas fa-tag"></i> ${holiday.holidayType}</p>
	                         </div>
	                         <div class="card-body-right">
	                             <div class="sun">
								 <img src="/images/admin/addHoliday.png" 
								 			onerror="this.src='https://cdn.pixabay.com/photo/2017/01/31/23/42/decorative-2028034_960_720.png'" 
								 			alt="holidayImg" width="200" height="200">
								 </div>
	                         </div>
	                     </div>
	                 `);
	             }
	             
	             function setupNavigation(holidays) {
	                 let currentIndex = 0;
	                 const slides = $('.holiday-slide');
	                 
	                 $('#prevBtn').click(function() {
	                     slides.eq(currentIndex).removeClass('active').addClass('next');
	                     currentIndex = (currentIndex - 1 + holidays.length) % holidays.length;
	                     slides.eq(currentIndex).removeClass('prev').addClass('active');
	                 });
	                 
	                 $('#nextBtn').click(function() {
	                     slides.eq(currentIndex).removeClass('active').addClass('prev');
	                     currentIndex = (currentIndex + 1) % holidays.length;
	                     slides.eq(currentIndex).removeClass('next').addClass('active');
	                 });
	             }
});