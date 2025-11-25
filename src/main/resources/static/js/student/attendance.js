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

    // Load marked months
    function loadMonths(semester) {
        $.ajax({
            url: '/student/marked-months',
            data: { semester: semester },
            success: function (months) {
                let container = $('#month-container');
                container.empty();
                if (months.length === 0) {
                    container.append(`<span class="no-months">No attendance available</span>`);
                    $('#attendance-head').empty();
                    $('#attendance-body').empty();
                    return;
                }
                months.forEach((m,index) => {
					let span=$(`<span class="month" data-month="${m}">${m}</span> `);
					container.append(span);
                    //container.append(`<span class="month" data-month="${m}">${m}</span>`);
					if(index===0){
						setTimeout(()=>span.trigger('click'),100);
					}
                });
            }
        });
    }

    function formatDate(rawDate) {
        const date = new Date(rawDate);
        const weekday = date.toLocaleDateString('en-US', { weekday: 'long' });
        const day = String(date.getDate()).padStart(2, '0');
        return `${weekday} ${day}`;
    }

    // Load month-wise attendance
    function loadAttendance(semester, month) {
        $.ajax({
            url: '/student/month-attendance',
            data: { semester: semester, month: month },
            success: function (response) {
                let tbody = $('#attendance-body');
                let hours = response.hours;
                let data = response.data;
                $('#attendance-head').empty();
                tbody.empty();

                if (!data || Object.keys(data).length === 0) {
                    tbody.append(`<tr><td colspan="${hours + 1}" class="no-data">No attendance data found for this month</td></tr>`);
                    return;
                }

                let headerRow = '<th>Date</th>';
                for (let h = 1; h <= hours; h++) {
                    headerRow += `<th>H${h}</th>`;
                }
                $('#attendance-head').html(`<tr>${headerRow}</tr>`);

                Object.keys(data).forEach(date => {
                    let formattedDate = formatDate(date);
                    let row = `<tr><td>${formattedDate}</td>`;

                    for (let h = 1; h <= hours; h++) {
                        let cellData = data[date][h];

                        if (cellData) {
                            let status = cellData.status;
                            let colorClass = status.toLowerCase();

                            row += `<td>
                                <span class="status ${colorClass}"
                                      data-status="${status}"
                                      data-subject="${cellData.subjectName}"
                                      data-subcode="${cellData.subjectCode}"
                                      data-markedby="${cellData.markedBy}">
                                </span>
                            </td>`;
                        } else {
                            row += `<td><span class="status unmarked"></span></td>`;
                        }
                    }

                    row += '</tr>';
                    tbody.append(row);
                });
            }
        });
    }

    // When semester dropdown changes
    $('#semesterDropdown').change(function () {
        let semester = $(this).val();
        loadMonths(semester);
    });

    // When a month is clicked
    $('#month-container').on('click', '.month', function () {
        $('.month').removeClass('active');
        $(this).addClass('active');

        let semester = $('#semesterDropdown').val();
        let month = new Date(Date.parse($(this).text() + " 1, 2020")).getMonth() + 1;
        loadAttendance(semester, month);
    });

    // Show dialog on status click
	$('#attendance-body').on('click', '.status', function () {
	    const status = $(this).data('status');
	    if (!status || status === 'UNMARKED') return;

	    const subject = $(this).data('subject');
	    const subcode = $(this).data('subcode');
	    const markedBy = $(this).data('markedby');

	    $('#dialogTitle').text(`${subject} (${subcode})`);
	    $('#dialogStatus').text(`Marked: ${status}`);
	    $('#dialogMarkedBy').text(`Marked By: ${markedBy}`);
	    $('#attendanceDialog').fadeIn(); // Show popup
	});

	// Close dialog on close button click
	$('#dialogClose').on('click', function () {
	    $('#attendanceDialog').fadeOut();
	});

	// Close dialog when clicking outside the dialog box
	$('#attendanceDialog').on('click', function (e) {
	    if ($(e.target).is('#attendanceDialog')) {
	        $(this).fadeOut();
	    }
	});



    // Initial load for default semester
    let defaultSemester = $('#semesterDropdown').val();
    loadMonths(defaultSemester);
});
