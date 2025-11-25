$(document).ready(function () {
    let studentData = {};
    let totalPages = 0;
    let currentPage = 0;

    // Show/Hide Loader
    function showLoader() { $("#spinner").show(); }
    function hideLoader() { $("#spinner").hide(); }

    // Semester Click
    $(".semester-book").click(function () {
        const semester = $(this).data("semester");
        loadSemesterData(semester);
    });

    // AJAX: Load semester data
    function loadSemesterData(semester) {
        showLoader();

        $.ajax({
            url: `/student/attendance/semester/${semester}`, // Adjust this to your backend endpoint
            type: "GET",
            dataType: "json",
            success: function (data) {
                studentData = data;
                openBook(semester);
            },
            error: function (xhr, status, error) {
                console.error("Failed to load semester data:", error);
                alert("Unable to load data. Please try again.");
                hideLoader();
            }
        });
    }

    // Open Book
    function openBook(semester) {
        $("#library-view").hide();
        $("#book-view").show();
        $("#student-name").text(studentData.name);
        $("#department").text(studentData.department);
        $("#semester-name").text(studentData.semesters[semester].name);

        setTimeout(() => {
            generatePages(semester);
            hideLoader();
            setTimeout(() => {
                $("#book-cover").addClass("open");
                setTimeout(() => {
                    showPage(0);
                }, 800);
            }, 100);
        }, 100);
    }

    // Generate Pages
    function generatePages(semester) {
        const bookPages = $("#book-pages");
        bookPages.empty();
        totalPages = 0;
        currentPage = 0;

        const months = Object.keys(studentData.semesters[semester].months);

        months.forEach((month) => {
            const monthData = studentData.semesters[semester].months[month];
            if (monthData.length > 0) {
                totalPages++;
                const page = $("<div>").addClass("page");

                const monthTitle = $("<h3>").text(month);
                const table = $("<table>");
                const thead = $("<thead>").append(`
                    <tr>
                        <th>Date</th>
                        <th colspan="6">Subjects</th>
                    </tr>
                `);
                table.append(thead);

                const tbody = $("<tbody>");
                monthData.forEach((entry) => {
                    const tr = $("<tr>");
                    tr.append($("<td>").text(entry.date));

                    entry.attendance.forEach((status) => {
                        const td = $("<td>").text(status.charAt(0).toUpperCase());
                        td.addClass(status); // apply status class: present, absent, etc.
                        tr.append(td);
                    });

                    tbody.append(tr);
                });

                table.append(tbody);
                page.append(monthTitle, table);
                bookPages.append(page);
            }
        });

        // Navigation Buttons
        const nav = $("<div>").attr("id", "page-nav");
        const prevBtn = $("<button>").attr("id", "prev-page").text("Previous").hide();
        const nextBtn = $("<button>").attr("id", "next-page").text("Next").hide();
        const backBtn = $("<button>").attr("id", "back-btn").text("Back");

        nav.append(prevBtn, nextBtn, backBtn);
        bookPages.append(nav);

        prevBtn.click(() => {
            if (currentPage > 0) showPage(currentPage - 1);
        });

        nextBtn.click(() => {
            if (currentPage < totalPages - 1) showPage(currentPage + 1);
        });

        backBtn.click(() => {
            $("#book-cover").removeClass("open");
            setTimeout(() => {
                $("#book-view").hide();
                $("#library-view").show();
                $("#book-pages").empty();
            }, 800);
        });
    }

    // Show Page
    function showPage(pageNum) {
        const pages = $(".page");
        pages.removeClass("active").eq(pageNum).addClass("active");
        currentPage = pageNum;

        $("#prev-page").toggle(pageNum > 0);
        $("#next-page").toggle(pageNum < totalPages - 1);
    }
});
