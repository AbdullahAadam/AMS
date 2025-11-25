$(document).ready(function () {
           let regNo = $("#regNo").text().trim();
           let semester = $("#semesterSelect").val();
		   let totalAttendanceContainer=$("#totalAttendanceContainer");
           let chartContainer = $("#attendanceChartContainer");		   
		   let noDataAnimation = $("#noDataAnimation");
		   // Initially hide both containers
		   totalAttendanceContainer.hide();
		   chartContainer.hide();
		   noDataAnimation.hide();
           $("#loadingSpinner").show();

           function animatePercentage(element, finalValue) {
               let currentValue = 0;
               let speed = 10;
               let increment = Math.ceil(finalValue / 20);
               
               let interval = setInterval(() => {
                   if (currentValue >= finalValue) {
                       clearInterval(interval);
                       element.text(finalValue + "%");
                   } else {
                       currentValue = Math.min(currentValue + increment, finalValue);
                       element.text(currentValue + "%");
                   }
               }, speed);
           }

           function loadAttendanceChart(semester) {
               $.ajax({
                   url: `/professor/mentee/attendance/${regNo}/${semester}`,
                   type: "GET",
                   success: function (data) {
                       console.log("Attendance Data:", data);
                       $("#loadingSpinner").hide();
					   
                       chartContainer.empty();
					   if (data.length === 0) {
							// No data available
							totalAttendanceContainer.hide();
					        chartContainer.hide();
					        noDataAnimation.show();
					   } else {
					        // Data available
					        noDataAnimation.hide();
							totalAttendanceContainer.show();
					        chartContainer.show();
							data.forEach((subject) => {
								let chartDiv = $("<div>").addClass("subject-chart");
								let canvasContainer = $("<div>").addClass("chart-container").appendTo(chartDiv);
								let canvas = $("<canvas>").appendTo(canvasContainer)[0];
								let ctx = canvas.getContext("2d");
								let color = subject.attendancePercentage >= 75 ? "#4CAF50" :
										                                          subject.attendancePercentage >= 50 ? "#FFC107" : "#F44336";
										                              // Create center text with subject ID
								let centerText = $(`<div class="center-text">${subject.attendancePercentage}%</div>`);
								canvasContainer.append(centerText);
								let chartInfo = $("<div>").addClass("chart-info");
								chartInfo.append(`<div class="subject-name">${subject.subjectName}</div>`);
								let subjectIdText = $(`<div class="subject-id">${subject.subjectId}</div>`);
								chartInfo.append(subjectIdText);
								chartDiv.append(chartInfo);
								chartContainer.append(chartDiv);
								// Create chart with final data immediately
								let attendanceChart = new Chart(ctx, {								
									type: "doughnut",
									data: {
										datasets: [{
											label: subject.subjectName,
										    data: [subject.attendancePercentage, 100 - subject.attendancePercentage],
										    backgroundColor: [color, "rgba(220, 220, 220, 0.2)"],
										    borderColor: [color, "rgba(0, 0, 0, 0.05)"],
										    borderWidth: 1,
										    borderRadius: 15,
										    spacing: 0,
										    hoverBorderWidth: 2
										}]
									},
									options: {
										responsive: true,
										maintainAspectRatio: true,
										cutout: "80%",
										rotation: -90,
										circumference: 360,
										animation: {
											duration: 800,
										    easing: "easeInOutQuad",
										    onComplete: function () {
												centerText.text(subject.attendancePercentage + "%");
										    }
									},
									plugins: {
										legend: { display: false },
										tooltip: {
											enabled: true,
										    callbacks: {
												label: function(context) {
													return `${context.label}: ${context.raw}%`;
										        }
										    }
										}
									}
								}
							});
						 });
										   
						} 
                   },
                   error: function () {
                       console.error("Error loading attendance data.");
                       $("#loadingSpinner").hide();
					   totalAttendanceContainer.hide();
					   chartContainer.hide();
					   noDataAnimation.show();
                   }
               });
           }
		   function updateProgressCircle(percentage) {
		   		       const card = $('#totalAttendanceContainer'); // Get the card element
		   		       const fill = $('.progress-circle-fill');
		   		       const fillOver50 = $('.progress-circle-fill-over50');
		   		       
		   		       // Reset classes
		   		       card.removeClass('good warning danger');
		   		       
		   		       // Set color based on percentage
		   		       if (percentage >= 75) {
		   		           card.addClass('good');
		   		       } else if (percentage >= 50) {
		   		           card.addClass('warning');
		   		       } else {
		   		           card.addClass('danger');
		   		       }
		   		       
		   		       // Rest of your rotation logic remains the same...
		   		       if (percentage <= 50) {
		   		           rotation = (percentage / 50) * 180;
		   		           fill.css('transform', `rotate(${rotation}deg)`);
		   		           fillOver50.css({
		   		               'opacity': '0',
		   		               'transform': 'rotate(0deg)'
		   		           });
		   		       } else {
		   		           fill.css('transform', 'rotate(180deg)');
		   		           rotation = ((percentage - 50) / 50) * 180;
		   		           fillOver50.css({
		   		               'opacity': '1',
		   		               'transform': `rotate(${rotation}deg)`
		   		           });
		   		       }
		   		       
		   		       $('.progress-circle-text').text(`${percentage}%`);
		   		       $('.progress-fill').css('width', `${percentage}%`);
		   		       $('.progress-percentage').text(`${percentage}%`);
		   		   }
		   function loadTotalAttendance(regNo, semester) {
		       let semesterNumber = semester.split(" ")[1];
		       $.ajax({
		           url: `/professor/mentee/attendance/total/${regNo}/${semesterNumber}`,
		           type: "GET",
		           success: function(response) {
		               let percentage = Math.round(response.totalAttendancePercentage);
		               let present = response.totalPresentPeriods;
		               let od = response.totalODPeriods;
		               let total = response.totalPeriods;
					   let late=response.totalLate;
					   let absent=response.totalAbsent;
		               // Update stats
		               $('.stat-present').text(present);
		               $('.stat-od').text(od);
		               $('.stat-total').text(total);
					   $('.stat-late').text(late);
					   $('.stat-absent').text(absent);
						
					   updateProgressCircle(percentage);
		               // Update progress circle
		               /*let rotation = (percentage / 100) * 180;
		               $('.progress-circle-fill').css('transform', `rotate(${rotation}deg)`);
		               $('.progress-circle-text').text(percentage + '%');
		               
		               // Update progress bar
		               $('.progress-fill').css('width', percentage + '%');
		               $('.progress-percentage').text(percentage + '%');
		               
		               // Apply color coding
		               let card = $('#totalAttendanceContainer');
		               card.removeClass('good warning danger');
		               
		               if (percentage >= 75) {
		                   card.addClass('good');
		               } else if (percentage >= 50) {
		                   card.addClass('warning');
		               } else {
		                   card.addClass('danger');
		               }*/
		           },
		           error: function() {
		               console.error("Error loading total attendance data");
		           }
		       });
		   }

           loadAttendanceChart(semester);
		   loadTotalAttendance(regNo, semester);
		   $("#backButton").click(function(){
				window.location.href="/professor/mentee";
		   });
           $("#semesterSelect").change(function () {
               let selectedSemester = $(this).val().split(" ")[1];
			   $("#loadingSpinner").show();
			   totalAttendanceContainer.hide();
			   chartContainer.hide();
			   noDataAnimation.hide();
			   loadTotalAttendance(regNo, selectedSemester);
               loadAttendanceChart(selectedSemester);
           });
		   $(".document-sheet:nth-child(1)").css("--rotation", "2deg");
		   $(".document-sheet:nth-child(2)").css("--rotation", "-1deg");
		   $(".document-sheet:nth-child(3)").css("--rotation", "0.5deg");
       });