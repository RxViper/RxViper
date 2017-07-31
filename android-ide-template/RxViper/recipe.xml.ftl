<?xml version="1.0"?>
<recipe>
    <#if !(hasDependency('com.dzaitsev.rxviper:rxviper'))>
        <dependency mavenUrl="com.dzaitsev.rxviper:rxviper:1.0.0-rc2"/>
    </#if>

    <#if supportKotlin>
        <instantiate
            from="root/src/app_package/ViewCallbacks.kt.ftl"
            to="${escapeXmlAttribute(srcOut)}/${name}ViewCallbacks.kt"
        />
    <#else>
        <instantiate
            from="root/src/app_package/ViewCallbacks.java.ftl"
            to="${escapeXmlAttribute(srcOut)}/${name}ViewCallbacks.java"
        />
    </#if>

    <#if supportKotlin>
        <instantiate
            from="root/src/app_package/Presenter.kt.ftl"
            to="${escapeXmlAttribute(srcOut)}/${name}Presenter.kt"
        />
    <#else>
        <instantiate
            from="root/src/app_package/Presenter.java.ftl"
            to="${escapeXmlAttribute(srcOut)}/${name}Presenter.java"
        />
    </#if>

    <#if generateInteractor>
        <#if supportKotlin>
            <instantiate
                from="root/src/app_package/Interactor.kt.ftl"
                to="${escapeXmlAttribute(srcOut)}/${name}Interactor.kt"
            />
        <#else>
            <instantiate
                from="root/src/app_package/Interactor.java.ftl"
                to="${escapeXmlAttribute(srcOut)}/${name}Interactor.java"
            />
        </#if>
    </#if>

    <#if generateRouter>
        <#if supportKotlin>
            <instantiate
                from="root/src/app_package/Router.kt.ftl"
                to="${escapeXmlAttribute(srcOut)}/${name}Router.kt"
            />
        <#else>
            <instantiate
                from="root/src/app_package/Router.java.ftl"
                to="${escapeXmlAttribute(srcOut)}/${name}Router.java"
            />
        </#if>
    </#if>

    <#if supportKotlin>
        <open file="${escapeXmlAttribute(srcOut)}/${name}Presenter.kt" />
    <#else>
        <open file="${escapeXmlAttribute(srcOut)}/${name}Presenter.java" />
    </#if>
</recipe>
