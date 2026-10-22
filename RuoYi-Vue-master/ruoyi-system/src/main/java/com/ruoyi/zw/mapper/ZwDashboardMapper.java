package com.ruoyi.zw.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface ZwDashboardMapper {

    /** 资产总数 */
    @Select("select count(*) from zw_asset where del_flag = '0'")
    int countAssetTotal();

    /** 正常资产数 */
    @Select("select count(*) from zw_asset where del_flag = '0' and status = '0'")
    int countAssetNormal();

    /** 故障资产数 */
    @Select("select count(*) from zw_asset where del_flag = '0' and status = '1'")
    int countAssetFault();

    /** 本月巡检完成次数 */
    @Select("select count(*) from zw_inspection_task where del_flag = '0' and status = '3' and date_format(actual_end_time, '%Y-%m') = date_format(curdate(), '%Y-%m')")
    int countMonthInspection();

    /** 本月计划巡检次数（按 plan_time 统计，不限 status） */
    @Select("select count(*) from zw_inspection_task where del_flag = '0' and date_format(plan_time, '%Y-%m') = date_format(curdate(), '%Y-%m')")
    int countMonthInspectionPlan();

    /** 本月工单总数 */
    @Select("select count(*) from zw_work_order where del_flag = '0' and date_format(create_time, '%Y-%m') = date_format(curdate(), '%Y-%m')")
    int countMonthWorkOrder();

    /** 本月完成工单数 */
    @Select("select count(*) from zw_work_order where del_flag = '0' and status = '3' and date_format(create_time, '%Y-%m') = date_format(curdate(), '%Y-%m')")
    int countMonthWorkOrderComplete();

    /** 巡检任务列表（近30天，用于趋势） */
    @Select("<script>select date_format(plan_time, '%Y-%m-%d') as stat_date, count(*) as inspection_count " +
            "from zw_inspection_task where del_flag = '0' and status = '3' " +
            "and plan_time between date_sub(curdate(), interval 30 day) and curdate() " +
            "group by date_format(plan_time, '%Y-%m-%d') order by stat_date asc</script>")
    List<Map<String, Object>> selectInspectionTrend();

    /** 工单趋势（近30天） */
    @Select("<script>select date_format(t.create_time, '%Y-%m-%d') as stat_date, count(*) as work_order_count " +
            "from zw_work_order t where t.del_flag = '0' " +
            "and t.create_time between date_sub(curdate(), interval 30 day) and curdate() " +
            "group by date_format(t.create_time, '%Y-%m-%d') order by stat_date asc</script>")
    List<Map<String, Object>> selectWorkOrderTrend();

    /** 健康度分布 */
    @Select("<script>select " +
            "case " +
            "  when health_score >= 90 then '优' " +
            "  when health_score >= 70 then '良' " +
            "  when health_score >= 50 then '中' " +
            "  else '差' end as health_level, " +
            "count(*) as cnt " +
            "from zw_asset where del_flag = '0' " +
            "group by health_level " +
            "order by cnt desc</script>")
    List<Map<String, Object>> selectHealthDistribution();

    /** 本月新增资产数 */
    @Select("select count(*) from zw_asset where del_flag = '0' and date_format(create_time, '%Y-%m') = date_format(curdate(), '%Y-%m')")
    int countMonthAssetNew();
}
