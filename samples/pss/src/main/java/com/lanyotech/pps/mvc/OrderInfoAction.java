package com.lanyotech.pps.mvc;

import java.util.List;

import com.easyjf.container.annonation.Action;
import com.easyjf.container.annonation.Inject;
import com.easyjf.core.support.ActionUtil;
import com.easyjf.core.support.query.QueryObject;
import com.easyjf.util.CommUtil;
import com.easyjf.web.Module;
import com.easyjf.web.Page;
import com.easyjf.web.WebForm;
import com.easyjf.web.core.AbstractPageCmdAction;
import com.easyjf.web.tools.IPageList;
import com.easyjf.web.tools.ListQuery;
import com.easyjf.web.tools.PageList;
import com.lanyotech.pps.domain.OrderInfo;
import com.lanyotech.pps.domain.OrderInfoItem;
import com.lanyotech.pps.query.BaseBillQuery;
import com.lanyotech.pps.query.OrderInfoItemQuery;
import com.lanyotech.pps.service.IBaseCountService;
import com.lanyotech.pps.service.IOrderInfoService;


/**
 * OrderInfoAction
 * @author EasyJWeb 1.0-m2
 * $Id: OrderInfoAction.java,v 0.0.1 2010-6-13 0:16:14 EasyJWeb 1.0-m3 with ExtJS Exp $
 */
@Action
public class OrderInfoAction extends AbstractPageCmdAction {
	
	@Inject
	private IOrderInfoService service;
	@Inject
	private IBaseCountService countService;
	/*
	 * set the current service
	 * return service
	 */
	public void setService(IOrderInfoService service) {
		this.service = service;
	}
	
	public void setCountService(IBaseCountService countService) {
		this.countService = countService;
	}

	public Page doIndex(WebForm f, Module m) {
		return page("list");
	}

	public Page doList(WebForm form) {
		QueryObject qo = form.toPo(BaseBillQuery.class);
		IPageList pageList = service.getOrderInfoBy(qo);
		form.jsonResult(pageList);
		return Page.JSONPage;
	}

	public Page doRemove(WebForm form) {
		Long id = new Long(CommUtil.null2String(form.get("id")));
		service.delOrderInfo(id);
		return pageForExtForm(form);
	}

	public Page doSave(WebForm form) {
		OrderInfo object = form.toPo(OrderInfo.class);
		this.handleItems(form, object);
		if (!hasErrors()){
			service.addOrderInfo(object);
			countService.recordNewSn("OrderInfo", new Integer(object.getSn()));
		}
		return pageForExtForm(form);
	}
	
	public Page doUpdate(WebForm form) {
		Long id = new Long(CommUtil.null2String(form.get("id")));
		OrderInfo object = service.getOrderInfo(id);
		form.toPo(object, true);
		this.handleItems(form, object);
		if (!hasErrors())
			service.updateOrderInfo(id, object);
		return pageForExtForm(form);
	}
	
	public Page doView(WebForm form) {
		Long id = new Long(CommUtil.null2String(form.get("id")));
		OrderInfo object = service.getOrderInfo(id);
		form.jsonResult(object.toJSonObjectWithItems());
		return Page.JSONPage;
	}
	protected void handleItems(WebForm form, OrderInfo object) {
		List list = ActionUtil.parseMulitItems(form, OrderInfoItem.class, new String[] { "id", "product", "price", "num", "amount", "remark" },
				"item_");
		List<Long> deletes=object.updateItems(list);
		if(!deletes.isEmpty()){
			for(Long id:deletes){
			this.service.delOrderInfoItem(id);
			}
		}
	}
	
	public Page doStatistics(WebForm form) {
		OrderInfoItemQuery qo=form.toPo(OrderInfoItemQuery.class);
		List list=service.statistics(qo, qo.getGroupBy());
		IPageList pageList=new PageList(new ListQuery(list));
		pageList.doList(-1, 1, "", "");
		form.jsonResult(pageList);
		return Page.JSONPage;
	}
}