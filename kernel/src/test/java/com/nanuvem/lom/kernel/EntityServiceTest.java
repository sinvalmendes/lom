package com.nanuvem.lom.kernel;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.nanuvem.lom.kernel.dao.memory.MemoryDaoFactory;

public class EntityServiceTest {

	private EntityService service;

	@Before
	public void init() {
		service = new EntityService(new MemoryDaoFactory());
	}

	@Test
	public void validNameAndNamespace() {
		createAndVerifyOneEntity("abc", "abc");
		createAndVerifyOneEntity("a.b.c", "abc");
		createAndVerifyOneEntity("a", "a");
		createAndVerifyOneEntity("abc123", "aaa");
		createAndVerifyOneEntity("abc", "abc1122");
	}

	@Test
	public void withoutNamespace() {
		createAndVerifyOneEntity("", "abc");
		createAndVerifyOneEntity(null, "abc");
		createAndVerifyOneEntity("", "a1");
		createAndVerifyOneEntity(null, "a1");
	}

	@Test
	public void twoEntitiesWithSameNameInDifferentNamespaces() {
		createAndVerifyTwoEntities("p1", "name", "p2", "name");
		createAndVerifyTwoEntities(null, "name", "p1", "name");
		createAndVerifyTwoEntities("a", "name", "a.b", "name");
	}

	@Test
	public void nameAndNamespaceWithSpaces() {
		this.expectExceptionOnCreateInvalidEntity("name space", "name",
				"Invalid value for Entity namespace: name space");
		this.expectExceptionOnCreateInvalidEntity("namespace", "na me",
				"Invalid value for Entity name: namespace.na me");
	}

	@Test
	public void withoutName() {
		this.expectExceptionOnCreateInvalidEntity("namespace", null,
				"The name of an Entity is mandatory");
		this.expectExceptionOnCreateInvalidEntity("namespace", "",
				"The name of an Entity is mandatory");
		this.expectExceptionOnCreateInvalidEntity(null, null,
				"The name of an Entity is mandatory");
		this.expectExceptionOnCreateInvalidEntity("", null,
				"The name of an Entity is mandatory");
	}

	@Test
	public void twoEntitiesWithSameNameInDefaultNamespace() {
		this.createAndVerifyOneEntity(null, "aaa");
		this.expectExceptionOnCreateInvalidEntity(null, "aaa",
				"The aaa entity already exists");
		this.expectExceptionOnCreateInvalidEntity("", "aaa",
				"The aaa entity already exists");
	}

	@Test
	public void twoEntitiesWithSameNameInAnonDefaultNamespace() {
		this.createAndVerifyOneEntity("a", "aaa");
		this.expectExceptionOnCreateInvalidEntity("a", "aaa",
				"The a.aaa entity already exists");
	}

	@Test
	public void nameAndNamespaceWithInvalidChars() {
		this.expectExceptionOnCreateInvalidEntity("a", "aaa$",
				"Invalid value for Entity name: aaa$");
		this.expectExceptionOnCreateInvalidEntity("a", "aaa#",
				"Invalid value for Entity name: aaa#");
		this.expectExceptionOnCreateInvalidEntity("a", "aaa=",
				"Invalid value for Entity name: aaa=");
		this.expectExceptionOnCreateInvalidEntity("a", "aaa.a",
				"Invalid value for Entity name: aaa.a");
		this.expectExceptionOnCreateInvalidEntity("a", "aaa/a",
				"Invalid value for Entity name: aaa/a");
		this.expectExceptionOnCreateInvalidEntity("a", "aaa*",
				"Invalid value for Entity name: aaa*");
		this.expectExceptionOnCreateInvalidEntity("a", "aaa'",
				"Invalid value for Entity name: aaa'");
		this.expectExceptionOnCreateInvalidEntity("a$", "aaa",
				"Invalid value for Entity namespace: a$");
		this.expectExceptionOnCreateInvalidEntity("a#", "aaa",
				"Invalid value for Entity namespace: a#");
		this.expectExceptionOnCreateInvalidEntity("a=", "aaa",
				"Invalid value for Entity namespace: a=");
		this.expectExceptionOnCreateInvalidEntity("a'", "aaa",
				"Invalid value for Entity namespace: a'");
		this.expectExceptionOnCreateInvalidEntity("a.", "aaa",
				"Invalid value for Entity namespace: a.");
		this.expectExceptionOnCreateInvalidEntity("a/a", "aaa",
				"Invalid value for Entity namespace: a/a");
		this.expectExceptionOnCreateInvalidEntity("a*", "aaa",
				"Invalid value for Entity namespace: a*");
	}

	@Test
	public void validNewNameAndPackage() {
		createUpdateAndVerifyOneEntity("a", "aaa1", "a.aaa1", "b", "bbb");
		createUpdateAndVerifyOneEntity("a", "aaa2", "a.aaa2", "a", "bbb");
		createUpdateAndVerifyOneEntity("a", "aaa3", "a.aaa3", "b", "aaa");
		createUpdateAndVerifyOneEntity("", "aaa1", "aaa1", "", "bbb");
		createUpdateAndVerifyOneEntity(null, "aaa2", "aaa2", null, "bbb");
		createUpdateAndVerifyOneEntity("a.b.c", "aaa1", "a.b.c.aaa1", "b",
				"bbb");
		createUpdateAndVerifyOneEntity("a.b.c", "aaa2", "a.b.c.aaa2", "b.c",
				"bbb");
	}

	@Test
	public void removePackageSetPackage() {
		createUpdateAndVerifyOneEntity("a", "aaa1", "a.aaa1", "", "aaa");
		createUpdateAndVerifyOneEntity("a", "aaa2", "a.aaa2", "", "bbb");
		createUpdateAndVerifyOneEntity("", "aaa1", "aaa1", "b", "bbb");
		createUpdateAndVerifyOneEntity("a", "aaa3", "a.aaa3", null, "aaa");
		createUpdateAndVerifyOneEntity("a", "aaa4", "a.aaa4", null, "bbb");
		createUpdateAndVerifyOneEntity(null, "aaa2", "aaa2", "b", "bbb");

		createUpdateAndVerifyOneEntity("a", "aaa5", "a.aaa5", "a", "aaa5");
		createUpdateAndVerifyOneEntity("a", "aaa6", "a.aaa6", "a", "aaa7");
		createUpdateAndVerifyOneEntity(null, "aaa3", "aaa3", null, "aaa4");
	}

	@Test
	public void renameCaousingTwoEntitiesWithSameNameInDifferentPackages() {
		Entity ea = new Entity();
		ea.setNamespace("a");
		ea.setName("aaa");
		service.create(ea);

		Entity eb = new Entity();
		eb.setNamespace("b");
		eb.setName("bbb");
		service.create(eb);

		service.update("c", "bbb", ea.getId(), ea.getVersion());
	}

	@Test
	public void moveCausingTwoEntitiesWithSameNameInDifferentPackages() {
		Entity ea = new Entity();
		ea.setNamespace("a");
		ea.setName("aaa");
		service.create(ea);

		Entity eb = new Entity();
		eb.setNamespace("b");
		eb.setName("bbb");
		service.create(eb);

		service.update("c", "bbb", ea.getId(), ea.getVersion());
	}

	@Test
	public void newNameAndPackageWithSpaces() {
		Entity ea = new Entity();
		ea.setNamespace("a");
		ea.setName("aaa");
		service.create(ea);
		try {
			service.update("name space", "aaa", ea.getId(), ea.getVersion());
			fail();
		} catch (MetadataException me) {
			Assert.assertEquals(
					"Invalid value for Entity namespace: name space",
					me.getMessage());
		}
		try {
			service.update("namespace", "na me", ea.getId(), ea.getVersion());
			fail();
		} catch (MetadataException me) {
			Assert.assertEquals(
					"Invalid value for Entity name: namespace.na me",
					me.getMessage());
		}
	}

	@Test
	public void removeName() {
		expectExceptionOnInvlidEntityUpdate("a", "aaa", "namespace", null,
				"The name of an Entity is mandatory");
		expectExceptionOnInvlidEntityUpdate("a", "aaa", "namespace", "",
				"The name of an Entity is mandatory");
		expectExceptionOnInvlidEntityUpdate("a", "aaa", null, null,
				"The name of an Entity is mandatory");
		expectExceptionOnInvlidEntityUpdate("a", "aaa", null, "",
				"The name of an Entity is mandatory");
	}

	@Test
	public void renameMoveCausingTwoEntitiesWithSameNameInDefaultPackage() {
		expectExceptionOnInvalidEntityUpdate("a", "aaa", "b", "bbb", "b",
				"bbb", "The b.bbb entity already exists");
		expectExceptionOnInvalidEntityUpdate("a", "aaa", "b", "aaa", "b",
				"bbb", "The b.bbb entity already exists");
		expectExceptionOnInvalidEntityUpdate("a", "aaa", "a", "bbb", "b",
				"bbb", "The b.bbb entity already exists");
		expectExceptionOnInvalidEntityUpdate("a.b.c", "aaa", "b.c", "bbb",
				"b.c", "bbb", "The b.c.bbb entity already exists");
		expectExceptionOnInvalidEntityUpdate("b.c", "aaa", "b.c", "bbb", "b.c",
				"bbb", "The b.c.bbb entity already exists");
		expectExceptionOnInvalidEntityUpdate("a.b.c", "bbb", "b.c", "bbb",
				"b.c", "bbb", "The b.c.bbb entity already exists");
	}

	@Test
	public void renameMoveCausingTwoEntitiesWithSameNameInAnonDefaultPackage() {
		expectExceptionOnInvalidEntityUpdate("a", "aaa", null, "bbb", null,
				"bbb", "The bbb entity already exists");
		expectExceptionOnInvalidEntityUpdate(null, "aaa", null, "bbb", null,
				"bbb", "The bbb entity already exists");
		expectExceptionOnInvalidEntityUpdate("a", "bbb", null, "bbb", null,
				"bbb", "The bbb entity already exists");
		expectExceptionOnInvalidEntityUpdate("a.b.c", "aaa", "", "bbb", "",
				"bbb", "The bbb entity already exists");
		expectExceptionOnInvalidEntityUpdate("", "aaa", "", "bbb", "", "bbb",
				"The bbb entity already exists");
		expectExceptionOnInvalidEntityUpdate("a.b.c", "bbb", "", "bbb", "",
				"bbb", "The bbb entity already exists");
	}

	@Test
	public void renameMoveCausingNameAndPackagesWithInvalidChars() {
		expectExceptionOnInvlidEntityUpdate("a", "aaa", "a", "aaa$",
				"Invalid value for Entity name: aaa$");
		expectExceptionOnInvlidEntityUpdate("a", "aaa", "a", "aaa#",
				"Invalid value for Entity name: aaa#");
		expectExceptionOnInvlidEntityUpdate("a", "aaa", "a", "aaa=",
				"Invalid value for Entity name: aaa=");
		expectExceptionOnInvlidEntityUpdate("a", "aaa", "a", "aaa'",
				"Invalid value for Entity name: aaa'");
		expectExceptionOnInvlidEntityUpdate("a", "aaa", "a", "aaa.a",
				"Invalid value for Entity name: aaa.a");
		expectExceptionOnInvlidEntityUpdate("a", "aaa", "a", "aaa/a",
				"Invalid value for Entity name: aaa/a");
		expectExceptionOnInvlidEntityUpdate("a", "aaa", "a", "aaa*",
				"Invalid value for Entity name: aaa*");
		expectExceptionOnInvlidEntityUpdate("a", "aaa", "a$", "aaa",
				"Invalid value for Entity namespace: a$");
		expectExceptionOnInvlidEntityUpdate("a", "aaa", "a#", "aaa",
				"Invalid value for Entity namespace: a#");
		expectExceptionOnInvlidEntityUpdate("a", "aaa", "a=", "aaa",
				"Invalid value for Entity namespace: a=");
		expectExceptionOnInvlidEntityUpdate("a", "aaa", "a'", "aaa",
				"Invalid value for Entity namespace: a'");
		expectExceptionOnInvlidEntityUpdate("a", "aaa", "a.", "aaa",
				"Invalid value for Entity namespace: a.");
		expectExceptionOnInvlidEntityUpdate("a", "aaa", "a/a", "aaa",
				"Invalid value for Entity namespace: a/a");
		expectExceptionOnInvlidEntityUpdate("a", "aaa", "a*", "aaa",
				"Invalid value for Entity namespace: a*");
	}

	@Test
	public void renameMoveForcingCaseInsentivePackagesAndNames() {
		expectExceptionOnInvalidEntityUpdate("a", "aaa", "b", "bbb", "b",
				"BbB", "The b.bbb entity already exists");
		expectExceptionOnInvalidEntityUpdate("a", "aaa", "b", "bbb", "b",
				"BBB", "The b.bbb entity already exists");
		expectExceptionOnInvalidEntityUpdate("a", "aaa", "CcC", "ccc", "ccc",
				"ccc", "The ccc.ccc entity already exists");
		expectExceptionOnInvalidEntityUpdate("a", "aaa", "CcC", "ccc", "CCC",
				"ccc", "The ccc.ccc entity already exists");
		expectExceptionOnInvalidEntityUpdate("a", "aaa", "CCC", "ccc", "ccc",
				"ccc", "The ccc.ccc entity already exists");
		expectExceptionOnInvalidEntityUpdate("a", "aaa", "CCC", "ccc", "ccc",
				"CCC", "The ccc.ccc entity already exists");
	}

	@Test
	public void invalidIdAndVersion() {
		EntityDTO createEntityDTO1 = new EntityDTO(null, null, "a", "aaa");
		EntityDTO updateEntityDTO1 = new EntityDTO(null, new Integer(0),
				"namespace", "name");
		expectExceptionOnInvalidEntityUpdate(createEntityDTO1,
				updateEntityDTO1, "The id of an Entity is mandatory on update");

		EntityDTO createEntityDTO2 = new EntityDTO(null, null, "a", "aaa");
		EntityDTO updateEntityDTO2 = new EntityDTO(createEntityDTO2.getId(),
				null, "a", "aaa");
		expectExceptionOnInvalidEntityUpdate(createEntityDTO2,
				updateEntityDTO2,
				"The version of an Entity is mandatory on update");

		EntityDTO createEntityDTO3 = new EntityDTO(null, null, "a", "aaa");
		EntityDTO updateEntityDTO3 = new EntityDTO(null, null, "a", "aaa");
		expectExceptionOnInvalidEntityUpdate(createEntityDTO3,
				updateEntityDTO3,
				"The version and id of an Entity are mandatory on update");

		EntityDTO createEntityDTO4 = new EntityDTO(null, null, "a", "aaa");
		EntityDTO updateEntityDTO4 = new EntityDTO(
				createEntityDTO4.getId() + 1, createEntityDTO4.getVersion(),
				"name", "aaa");
		expectExceptionOnInvalidEntityUpdate(createEntityDTO4,
				updateEntityDTO4, "Invalid id for Entity a.aaa");

		EntityDTO createEntityDTO5 = new EntityDTO(null, null, "a", "aaa");
		EntityDTO updateEntityDTO5 = new EntityDTO(
				createEntityDTO4.getId() + 1, new Integer(-1), "namespace",
				"name");
		expectExceptionOnInvalidEntityUpdate(
				createEntityDTO5,
				updateEntityDTO5,
				"Updating a deprecated version of Entity a.aaa. Get the entity again to obtain the newest version and proceed updating.");
	}

	@Test
	public void severalUpdates() {
		Entity ea = new Entity();
		ea.setNamespace("a");
		ea.setName("aaa");
		service.create(ea);

		service.update("b", "abc", ea.getId(), ea.getVersion());
		service.update("a.b", "abc", ea.getId(), ea.getVersion() + 1);
		service.update(null, "abc", ea.getId(), ea.getVersion() + 2);
		service.update("a.b.c", "abc", ea.getId(), ea.getVersion() + 3);

		Entity found = service.findEntityById(ea.getId());
		Assert.assertEquals("a.b.c", found.getNamespace());
		Assert.assertEquals("abc", found.getName());
		Assert.assertEquals(new Long(1), found.getId());
		Assert.assertEquals(new Integer(4), found.getVersion());

	}

	@Test
	public void listAllEntities() {
		List<Entity> allEntities = service.listAll();
		Assert.assertEquals(0, allEntities.size());

		Entity entity1 = this.createAndSaveOneEntity("ns1", "n1");
		Entity entity2 = this.createAndSaveOneEntity("ns2", "n2");
		Entity entity3 = this.createAndSaveOneEntity("ns2", "n3");

		allEntities = service.listAll();

		Assert.assertEquals(3, allEntities.size());
		Assert.assertEquals(entity1, allEntities.get(0));
		Assert.assertEquals(entity2, allEntities.get(1));
		Assert.assertEquals(entity3, allEntities.get(2));

		service.remove(entity1);
		service.remove(entity2);
		service.remove(entity3);

		Assert.assertEquals(0, service.listAll().size());
	}

	@Test
	public void listEntitiesByValidFragmentOfNameAndPackage() {
		String namespaceFragment = "ns";
		String nameFragment = "n";
		List<Entity> allEntities = service
				.listEntitiesByFragmentOfNameAndPackage(namespaceFragment,
						nameFragment);

		Assert.assertEquals(0, allEntities.size());
		Entity entity1 = this.createAndSaveOneEntity("ns1", "n1");
		Entity entity2 = this.createAndSaveOneEntity("ns2", "n2");
		Entity entity3 = this.createAndSaveOneEntity("ns2", "n3");

		allEntities = service.listEntitiesByFragmentOfNameAndPackage(
				namespaceFragment, nameFragment);

		Assert.assertEquals(3, allEntities.size());
		Assert.assertEquals(entity1, allEntities.get(0));
		Assert.assertEquals(entity2, allEntities.get(1));
		Assert.assertEquals(entity3, allEntities.get(2));

		service.remove(entity1);
		service.remove(entity2);
		service.remove(entity3);

		Assert.assertEquals(0, service.listAll().size());

	}

	@Test
	public void listEntitiesByEmptyNameAndPackage() {
		List<Entity> allEntities = service
				.listEntitiesByFragmentOfNameAndPackage("", "");
		Assert.assertEquals(0, allEntities.size());

		allEntities = service.listEntitiesByFragmentOfNameAndPackage("ns", "");
		Assert.assertEquals(0, allEntities.size());

		allEntities = service.listEntitiesByFragmentOfNameAndPackage("", "n");
		Assert.assertEquals(0, allEntities.size());

		Entity entity1 = this.createAndSaveOneEntity("ns1", "n1");
		Entity entity2 = this.createAndSaveOneEntity("ns2", "n2");
		Entity entity3 = this.createAndSaveOneEntity("ns2", "n3");
		List<Entity> expectedEntities = new ArrayList<Entity>();
		expectedEntities.add(entity1);
		expectedEntities.add(entity2);
		expectedEntities.add(entity3);

		listEntitiesByFragmentOfNamespaceAndNamesAndVerifyThatEntitiesWhereListed(
				expectedEntities, "", "");

		listEntitiesByFragmentOfNamespaceAndNamesAndVerifyThatEntitiesWhereListed(
				expectedEntities, null, "");

		listEntitiesByFragmentOfNamespaceAndNamesAndVerifyThatEntitiesWhereListed(
				expectedEntities, "", null);

		listEntitiesByFragmentOfNamespaceAndNamesAndVerifyThatEntitiesWhereListed(
				expectedEntities, "ns", "");

		listEntitiesByFragmentOfNamespaceAndNamesAndVerifyThatEntitiesWhereListed(
				expectedEntities, "", "n");

		allEntities = service.listEntitiesByFragmentOfNameAndPackage("nspace",
				"");
		Assert.assertEquals(0, allEntities.size());

		for (Entity entity : expectedEntities) {
			service.remove(entity);
		}

		Assert.assertEquals(0, service.listAll().size());
	}

	@Test
	public void entitiesWithDefaultPackageMustAppearWhenPackageIsNotGiven() {
		Entity entity1 = this.createAndSaveOneEntity("ns1", "n1");
		Entity entity2 = this.createAndSaveOneEntity("ns2", "n2");
		Entity entity3 = this.createAndSaveOneEntity("ns2", "n3");
		List<Entity> expectedEntities = new ArrayList<Entity>();
		expectedEntities.add(entity1);
		expectedEntities.add(entity2);
		expectedEntities.add(entity3);

		listEntitiesByFragmentOfNamespaceAndNamesAndVerifyThatEntitiesWhereListed(
				expectedEntities, null, "");

		listEntitiesByFragmentOfNamespaceAndNamesAndVerifyThatEntitiesWhereListed(
				expectedEntities, "", null);

		listEntitiesByFragmentOfNamespaceAndNamesAndVerifyThatEntitiesWhereListed(
				expectedEntities, null, null);

		List<Entity> allEntities = service
				.listEntitiesByFragmentOfNameAndPackage(null, "nspace");
		Assert.assertEquals(0, allEntities.size());

		allEntities = service.listEntitiesByFragmentOfNameAndPackage(null,
				"name");
		Assert.assertEquals(0, allEntities.size());

		listEntitiesByFragmentOfNamespaceAndNamesAndVerifyThatEntitiesWhereListed(
				expectedEntities, "ns", "");

		listEntitiesByFragmentOfNamespaceAndNamesAndVerifyThatEntitiesWhereListed(
				expectedEntities, "", "n");

		for (Entity entity : expectedEntities) {
			service.remove(entity);
		}

		Assert.assertEquals(0, service.listAll().size());

	}

	@Test
	public void listEntitiesByFragmentOfNameAndPackagesWithSpaces() {
		expectExceptionOnInvalidListEntity(null, "na me",
				"Invalid value for Entity name: na me");
		expectExceptionOnInvalidListEntity("name space", null,
				"Invalid value for Entity namespace: name space");
		expectExceptionOnInvalidListEntity("namespace", "na me",
				"Invalid value for Entity name: namespace.na me");
		expectExceptionOnInvalidListEntity("name space", "name",
				"Invalid value for Entity namespace: name space");
	}

	@Test
	public void listEntitiesForcingCaseInsensitivePackagesAndNames() {
		Entity entity1 = this.createAndSaveOneEntity("ns1", "n1");
		Entity entity2 = this.createAndSaveOneEntity("NS2", "n2");
		Entity entity3 = this.createAndSaveOneEntity("NS3", "N3");
		List<Entity> expectedEntities = new ArrayList<Entity>();
		expectedEntities.add(entity1);
		expectedEntities.add(entity2);
		expectedEntities.add(entity3);

		listEntitiesByFragmentOfNamespaceAndNamesAndVerifyThatEntitiesWhereListed(
				expectedEntities, "ns", "n");

		listEntitiesByFragmentOfNamespaceAndNamesAndVerifyThatEntitiesWhereListed(
				expectedEntities, "NS", "n");

		listEntitiesByFragmentOfNamespaceAndNamesAndVerifyThatEntitiesWhereListed(
				expectedEntities, "ns", null);

		listEntitiesByFragmentOfNamespaceAndNamesAndVerifyThatEntitiesWhereListed(
				expectedEntities, null, "N");

		listEntitiesByFragmentOfNamespaceAndNamesAndVerifyThatEntitiesWhereListed(
				expectedEntities, "N", "N");
	}

	@Test
	public void listEntitiesUsingInvalidFragmentOfNameAndPackage() {
		expectExceptionOnInvalidListEntity("ns", "n$",
				"Invalid value for Entity name: n$");
		expectExceptionOnInvalidListEntity("ns", "n#",
				"Invalid value for Entity name: n#");
		expectExceptionOnInvalidListEntity("ns", "n=",
				"Invalid value for Entity name: n=");
		expectExceptionOnInvalidListEntity("ns", "n'",
				"Invalid value for Entity name: n'");
		expectExceptionOnInvalidListEntity("ns", "n.n",
				"Invalid value for Entity name: n.n");
		expectExceptionOnInvalidListEntity("ns", "n/n",
				"Invalid value for Entity name: n/n");
		expectExceptionOnInvalidListEntity("ns", "n*",
				"Invalid value for Entity name: n*");
		expectExceptionOnInvalidListEntity("ns$", "n",
				"Invalid value for Entity namespace: ns$");
		expectExceptionOnInvalidListEntity("ns#", "n",
				"Invalid value for Entity namespace: ns#");
		expectExceptionOnInvalidListEntity("ns=", "n",
				"Invalid value for Entity namespace: ns=");
		expectExceptionOnInvalidListEntity("ns'", "n",
				"Invalid value for Entity namespace: ns'");
		expectExceptionOnInvalidListEntity("ns.", "n",
				"Invalid value for Entity namespace: ns.");
		expectExceptionOnInvalidListEntity("ns/a", "n",
				"Invalid value for Entity namespace: ns/a");
		expectExceptionOnInvalidListEntity("ns*", "n",
				"Invalid value for Entity namespace: ns*");
	}

	@Test
	public void getEntityByValidNameAndPackage() {
		expectExceptionOnInvalidListEntity("ns", "n", "Entity not found: ns.n");
		Entity entity1 = createEntity("ns1", "n1");
		Entity foundEntity1 = service.readEntityByNamespaceAndName("ns1", "n1");
		Assert.assertEquals(entity1, foundEntity1);

		Entity entity2 = createEntity("ns2", "n2");
		Entity foundEntity2 = service.readEntityByNamespaceAndName("ns2", "n2");
		Assert.assertEquals(entity2, foundEntity2);

		expectExceptionOnInvalidGetEntity("ns1", "n", "Entity not found: ns1.n");
		expectExceptionOnInvalidGetEntity("ns", "n1", "Entity not found: ns.n1");
		expectExceptionOnInvalidGetEntity("ns2", "n1",
				"Entity not found: ns2.n1");
		List<Entity> allEntities = service.listAll();
		Assert.assertEquals(2, allEntities.size());
		Assert.assertEquals(entity1, allEntities.get(0));
		Assert.assertEquals(entity2, allEntities.get(1));
	}

	@Test
	public void getEntityByEmptyNameAndPackage() {
		createEntity("ns1", "n1");
		Entity entity2 = createEntity("ns2", "n2");
		expectExceptionOnInvalidGetEntity("", "n1", "Entity not found: n1");

		Entity foundEntity2 = service.readEntityByNamespaceAndName(null, "n2");
		Assert.assertEquals(entity2, foundEntity2);

		expectExceptionOnInvalidGetEntity("ns1", "", "Entity not found: ns1");
	}

	@Test
	public void getEntityByNameAndPackageWithSpaces() {
		expectExceptionOnInvalidGetEntity("", "na me",
				"Invalid key for Entity: na me");
		expectExceptionOnInvalidGetEntity("name space", "name",
				"Invalid key for Entity: name space.name");
		expectExceptionOnInvalidGetEntity("namespace", "na me",
				"Invalid key for Entity: namespace.na me");
	}

	@Test
	public void getEntityForcingCaseInsensitivePackagesAndNames() {
		Entity entity = createEntity("nS", "nA");
		Entity ea = service.readEntityByNamespaceAndName("ns", "na");
		Assert.assertEquals(entity, ea);

		ea = service.readEntityByNamespaceAndName("NS", "NA");
		Assert.assertEquals(entity, ea);

		ea = service.readEntityByNamespaceAndName("nS", "nA");
		Assert.assertEquals(entity, ea);

		ea = service.readEntityByNamespaceAndName("NS", "na");
		Assert.assertEquals(entity, ea);

		ea = service.readEntityByNamespaceAndName("ns", "NA");
		Assert.assertEquals(entity, ea);

		ea = service.readEntityByNamespaceAndName("Ns", "Na");
		Assert.assertEquals(entity, ea);

	}

	@Test
	public void getEntityUsingInvalidNameAndPackage() {
		expectExceptionOnInvalidGetEntity("ns", "n$",
				"Invalid key for Entity: ns.n$");
		expectExceptionOnInvalidGetEntity("ns", "n#",
				"Invalid key for Entity: ns.n#");
		expectExceptionOnInvalidGetEntity("ns", "n=",
				"Invalid key for Entity: ns.n=");
		expectExceptionOnInvalidGetEntity("ns", "n/n",
				"Invalid key for Entity: ns.n/n");
		expectExceptionOnInvalidGetEntity("ns", "n*",
				"Invalid key for Entity: ns.*");
		expectExceptionOnInvalidGetEntity("ns", "n'",
				"Invalid key for Entity: ns.'");
		expectExceptionOnInvalidGetEntity("ns$", "n",
				"Invalid key for Entity: ns$.n");
		expectExceptionOnInvalidGetEntity("ns#", "n",
				"Invalid key for Entity: ns#.n");
		expectExceptionOnInvalidGetEntity("ns=", "n",
				"Invalid key for Entity: ns=.n");
		expectExceptionOnInvalidGetEntity("ns/", "n",
				"Invalid key for Entity: ns/.n");
		expectExceptionOnInvalidGetEntity("ns*", "n",
				"Invalid key for Entity: ns*.n");
		expectExceptionOnInvalidGetEntity("ns'", "n",
				"Invalid key for Entity: ns'.n");
	}

	private Entity createEntity(String namespace, String name) {
		Entity entity = new Entity();
		entity.setName(name);
		entity.setNamespace(namespace);
		service.create(entity);
		return entity;
	}

	private void listEntitiesByFragmentOfNamespaceAndNamesAndVerifyThatEntitiesWhereListed(
			List<Entity> expectedListedEntities, String namespaceFragment,
			String nameFragment) {

		List<Entity> allEntities = service
				.listEntitiesByFragmentOfNameAndPackage(namespaceFragment,
						nameFragment);
		Assert.assertEquals(expectedListedEntities.size(), allEntities.size());
		int index = 0;
		for (Entity expected : expectedListedEntities) {
			Assert.assertEquals(expected, allEntities.get(index));
			index++;
		}

	}

	private void expectExceptionOnInvalidGetEntity(String namespace,
			String name, String expectedMessage) {
		try {
			service.readEntityByNamespaceAndName(namespace, name);
			fail();
		} catch (MetadataException me) {
			Assert.assertEquals(me.getMessage(), expectedMessage);
		}
	}

	private void expectExceptionOnInvalidListEntity(String namespaceFragment,
			String nameFragment, String expectedMessage) {
		try {
			service.listEntitiesByFragmentOfNameAndPackage(namespaceFragment,
					nameFragment);
			fail();
		} catch (MetadataException me) {
			Assert.assertEquals(me.getMessage(), expectedMessage);
		}
	}

	private void expectExceptionOnInvalidEntityUpdate(
			String firstentitynamespace, String firstentityname,
			String secondentitynamespace, String secondentityname,
			String firstentitynamespaceupdate, String firstentitynameupdate,
			String expectedExceptionMessage) {

		Entity ea = new Entity();
		ea.setNamespace(firstentitynamespace);
		ea.setName(firstentityname);
		service.create(ea);

		Entity eb = new Entity();
		eb.setNamespace(secondentitynamespace);
		eb.setName(secondentityname);
		service.create(eb);
		try {
			service.update(firstentitynamespaceupdate, firstentitynameupdate,
					ea.getId(), ea.getVersion());
			fail();
		} catch (MetadataException me) {
			Assert.assertEquals(expectedExceptionMessage, me.getMessage());
		}
	}

	private void expectExceptionOnInvalidEntityUpdate(
			EntityDTO createEntityDTO, EntityDTO updateEntityDTO,
			String expectedMessage) {
		Entity entity = new Entity();
		entity.setNamespace(createEntityDTO.getNamespace());
		entity.setName(createEntityDTO.getName());
		service.create(entity);
		createEntityDTO.setId(entity.getId());
		createEntityDTO.setVersion(entity.getVersion());
		try {
			service.update(updateEntityDTO.getNamespace(),
					updateEntityDTO.getName(), entity.getId(),
					entity.getVersion());
			fail();
		} catch (MetadataException me) {
			Assert.assertEquals(expectedMessage, me.getMessage());
		}
	}

	private void expectExceptionOnInvlidEntityUpdate(String firstnamespace,
			String firstname, String secondnamespace, String secondname,
			String expectedMessage) {
		Entity entity = new Entity();
		entity.setNamespace(firstnamespace);
		entity.setName(firstname);
		service.create(entity);
		try {
			service.update(secondnamespace, secondname, entity.getId(),
					entity.getVersion());
			fail();
		} catch (MetadataException me) {
			Assert.assertEquals(expectedMessage, me.getMessage());
		}
	}

	private void expectExceptionOnCreateInvalidEntity(String namespace,
			String name, String expectedMessage) {
		try {
			createAndVerifyOneEntity(namespace, name);
			fail();
		} catch (MetadataException e) {
			Assert.assertEquals(expectedMessage, e.getMessage());
		}
	}

	private void createUpdateAndVerifyOneEntity(String firstNamespace,
			String firstName, String updatePath, String secondNamespace,
			String secondName) {

		Entity entity = new Entity();
		entity.setNamespace(firstNamespace);
		entity.setName(firstName);
		service.create(entity);

		Assert.assertNotNull(entity.getId());
		Assert.assertEquals((Integer) 0, entity.getVersion());

		Entity entity1 = service.update("secondNamespace", "secondName",
				entity.getId(), entity.getVersion());

		List<Entity> allEntities = service.listAll();
		Entity entityFound = allEntities.get(0);

		Assert.assertEquals((Integer) 1, entity1.getVersion());
		Assert.assertNotSame(entity, entityFound);
		service.remove(entity);
		service.remove(entity1);
	}

	private void createAndVerifyTwoEntities(String string, String string2,
			String string3, String string4) {
		Entity entity1 = new Entity();
		service.create(entity1);

		Entity entity2 = new Entity();
		service.create(entity2);

		Assert.assertNotNull(entity1.getId());
		Assert.assertNotNull(entity2.getId());

		Assert.assertEquals((Integer) 0, entity1.getVersion());
		Assert.assertEquals((Integer) 0, entity2.getVersion());

		List<Entity> entities = service.listAll();
		Assert.assertEquals(2, entities.size());
		Assert.assertEquals(entity1, entities.get(0));
		Assert.assertEquals(entity2, entities.get(1));

		service.remove(entity1);
		service.remove(entity2);
	}

	private Entity createAndSaveOneEntity(String namespace, String name) {
		Entity entity = new Entity();
		entity.setNamespace(namespace);
		entity.setName(name);
		service.create(entity);

		Assert.assertNotNull(entity.getId());
		Assert.assertEquals((Integer) 0, entity.getVersion());
		return entity;
	}

	private void createAndVerifyOneEntity(String name, String namespace) {
		Entity entity = new Entity();
		entity.setNamespace(namespace);
		entity.setName(name);
		service.create(entity);

		Assert.assertNotNull(entity.getId());
		Assert.assertEquals((Integer) 0, entity.getVersion());

		List<Entity> entities = service.listAll();
		Assert.assertEquals(1, entities.size());
		Assert.assertEquals(entity, entities.get(0));

		service.remove(entity);
	}

}
