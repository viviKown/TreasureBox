let openTag = {};
/**
 * @method getProject
 * @description 获取所有项目名
 * @param username
 * @param password
 */
$(function () {
    $.ajax({
        url: "http://mantis.tclking.com:8090/get_data.php?action=project",
        type: "post",
        dataType: "json",
        contentType: "text/plain",
        data: "{\r\n\"username\":\"xiaohong1.guo\",\r\n\"password\":\"guo#0108\"\r\n}",
        success: function (response) {
            let _html = "";
            _html += '<ul>';
            for (let i = 0; i < response.data.length; i++) {
                _html += `<li onclick="getCategory(${response.data[i].id},${i},'${response.data[i].name}')">
                <i class="arrRight" data-name="${response.data[i].id}"></i>
                <cite>${response.data[i].name}</cite></li>
                <div id="${response.data[i].id}"></div>`
            }
            _html += '</ul>';
            $('#firstUI').html(_html)
        },
        error: function (error) {
            alert("请求出错" + error.toString());
        }
    });
});

/**
 * @method getCategory
 * @description 根据项目ID获取项目的所有分类名
 * @param projectId
 * @param index 控件索引
 * @param projectName
 */
function getCategory(projectId, index, projectName) {
    if (openTag[projectId]) {
        $("#" + projectId).hide();
        $(`[data-name = ${projectId}]`).removeClass('arrDown').addClass('arrRight');//切换三角形
        openTag[projectId] = false;
        return
    }
    if ($(`#${projectId}`).html()) {
        $("#" + projectId).show();
        $(`[data-name = ${projectId}]`).removeClass('arrRight').addClass('arrDown');
        openTag[projectId] = true;
        return
    }
    $.ajax({
        url: "http://mantis.tclking.com:8090/get_data.php?action=category&project_id=" + projectId,
        type: "post",
        dataType: "json",
        contentType: "text/plain",
        async: false,
        data: "{\r\n\"username\":\"xiaohong1.guo\",\r\n\"password\":\"guo#0108\"\r\n}",
        success: function (response) {
            let _html = '';
            _html += '<ul>';
            for (let i = 0; i < response.data.length; i++) {
                _html += `<li onclick="getBug(${response.data[i].id}${index},'${projectName}',${response.data[i].id})">
                <i class="arrRight" data-name="third${response.data[i].id}"></i>
                <cite>${response.data[i].name}</cite></li>
                <div id="third${response.data[i].id}${index}"></div>`
            }
            _html += '</ul>';
            $("#" + projectId).html(_html);
            openTag[projectId] = true;
            $(`[data-name = ${projectId}]`).removeClass('arrRight').addClass('arrDown');
        },
        error: function (error) {
            alert("请求出错" + error.toString());
        }
    });
}

/**
 * @method getBug
 * @description 根据项目名和分类号获取当前项目，当前分类下所有release的bug
 * @param id 控件id
 * @param project 项目名
 * @param categoryId 分类id号
 */
function getBug(id, project, categoryId) {
    if (openTag["third" + id]){
        $("#third" + id).hide();
        $(`[data-name = third${categoryId}]`).removeClass('arrDown').addClass('arrRight');
        openTag["third" + id] = false;
        return
    }
    if ($(`#third${id}`).html()) {
        $("#third" + id).show();
        $(`[data-name = third${categoryId}]`).removeClass('arrRight').addClass('arrDown');
        openTag["third" + id] = true;
        return
    }
    $.ajax({
        url: "http://mantis.tclking.com:8090/get_data.php?action=releaselist&project=" + project,
        type: "post",
        dataType: "json",
        contentType: "text/plain",
        async: false,
        data: "{\r\n\"username\":\"xiaohong1.guo\",\r\n\"password\":\"guo#0108\"\r\n}",
        success: function (response) {
            let _html = '';
            _html += '<ul>';
            for (let i = 0; i < response.data.length; i++) {
                if (Number(response.data[i].category) === categoryId) {
                    _html += `<li>${response.data[i].summary}</li>`
                }
            }
            _html += '</ul>';
            $("#third" + id).html(_html === '<ul></ul>' ? '暂无' : _html);
            openTag["third" + id] = true;
            $(`[data-name = third${categoryId}]`).removeClass('arrRight').addClass('arrDown');
        },
        error: function (error) {
            alert("请求出错" + error.toString());
        }
    });
}